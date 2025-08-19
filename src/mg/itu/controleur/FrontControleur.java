/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package mg.itu.controleur;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.adapter.LocalDateAdapter;
import mg.itu.adapter.LocalDateTimeAdapter;
import mg.itu.adapter.LocalTimeAdapter;
import mg.itu.annotation.Controleur;
import mg.itu.annotation.GET;
import mg.itu.annotation.POST;
import mg.itu.annotation.Url;
import mg.itu.exception.ReponseException;
import mg.itu.security.handler.SecurityHandler;
import mg.itu.util.Mapping;

@MultipartConfig
public class FrontControleur extends HttpServlet {
    private final String INIT_PACKAGE = "package_controleur";

    private Map<String, Mapping> controleurs = new HashMap<>();

    private void scannePackage(String cPackage) throws Exception {
        if (cPackage == null) {
            ServletContext sc = getServletContext();
            cPackage = sc.getInitParameter(INIT_PACKAGE);
        }

        String path = cPackage.replace(".", "/");
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if(url == null) {
            throw new Exception("Le package ["+ cPackage +"] n'existe pas");
        }

        File directory = new File(url.getFile());
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if(file.isFile() && file.getName().endsWith(".class")) {
                    String className = cPackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> class1 = Class.forName(className);
                    Annotation annotation = class1.getAnnotation(Controleur.class);
                    if (annotation != null) {
                        this.setMapping(class1);
                    }
                } else if (file.isDirectory()) {
                    String newPackage = cPackage + "." + file.getName();
                    scannePackage(newPackage);
                }
            }
        }
    }

    private void setMapping(Class<?> c) throws Exception {
        Method[] methodes = c.getMethods();
        String bas_url = c.getAnnotation(Controleur.class).path();
        for (int j = 0; j < methodes.length; j++) {
            Url annotUrl = methodes[j].getAnnotation(Url.class);
            if ( annotUrl !=null ) {
                URI uri = Paths.get(this.getServletContext().getContextPath(),bas_url, annotUrl.value()).toUri();
                String url = uri.getPath();
                System.out.println("[SCAN]: "+ url);
                Mapping map;
                if (controleurs.containsKey(url)) {
                    map = controleurs.get(url);
                } else {
                    map = new Mapping();
                }

                if (methodes[j].isAnnotationPresent(POST.class)) {
                    map.addVerbAction("POST", c, methodes[j]);
                }
                if(methodes[j].isAnnotationPresent(GET.class)) {
                    map.addVerbAction("GET", c, methodes[j]);
                }
                if(!methodes[j].isAnnotationPresent(GET.class) && !methodes[j].isAnnotationPresent(POST.class)) {
                    map.addVerbAction("GET", c, methodes[j]);
                }
                controleurs.put(url, map);
            }
        }
    }

    private String getRequestUrl(HttpServletRequest request) throws URISyntaxException {
        String requestUrl = Paths.get(request.getRequestURI()).toUri().getPath();
        return requestUrl;
    }

    protected String getVeritableUrl(String url) throws URISyntaxException {
        URI uri = new URI(getServletContext().getContextPath());
        return uri.resolve(url).toString();
    }

    protected void handleResponse(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Gestion de reponse
        Object rep = mapping.getResponse(request);
        if(rep == null) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "Pas de type de retour");
            return;
        }
        //Validation error
        if (rep instanceof HttpServletRequest) {
            String link = (String) request.getSession().getAttribute(Mapping.ATR_VALIDATION);
            if(link == null){ response.sendError(500, "Page validation erreur non configurer"); return;}
            link = getVeritableUrl(link);
            RequestDispatcher dispatcher = ((HttpServletRequest) rep).getRequestDispatcher(link);
            dispatcher.forward((HttpServletRequest) rep, response);
        } else if (mapping.isRestapi(request.getMethod())) {
            PrintWriter out = response.getWriter();
            response.setContentType("text/json");
            Gson json = prepareGson();
            if (rep instanceof ModelView) {
                ModelView mv = (ModelView) rep;
                out.println(json.toJson(mv.getData()));
            } else if (rep instanceof String) {
                out.println(rep);
            } else {
                out.println(json.toJson(rep));
            }
        } else if(rep instanceof String) {
            PrintWriter out = response.getWriter();
            out.println(rep.toString());
        } else if (rep instanceof ModelView) {
            ModelView mv = (ModelView) rep;
            mv.prepareRequestDispatcher(request);
            RequestDispatcher dispatcher = mv.getDispatcher();
            mv.setAttributs();
            dispatcher.forward(mv.getRequest(), response);
        } else if(rep instanceof mg.itu.util.File) {
            mg.itu.util.File file = (mg.itu.util.File) rep;
            response.setContentType(file.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            response.setContentLength(file.getContent().length);
            response.getOutputStream().write(file.getContent());
        } else {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Type de retour non supporter");
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        SecurityHandler.request = request;
        try {
            String requestUrl = getRequestUrl(request);
            Mapping mapping = controleurs.getOrDefault(requestUrl, null); 
            if (mapping == null) {
                // response.sendError(HttpServletResponse.SC_NOT_FOUND,  "La ressource demandée ["+requestUrl+"] n'est pas disponible");
                renderErrorPage(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "La ressource demandée ["+requestUrl+"] n'est pas disponible",
                    requestUrl);

                return;
            }

            if (!mapping.isMethodAllowed(request.getMethod())) {
                renderErrorPage(response,
                        HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        "Méthode HTTP non autorisée",
                        request.getRequestURI());
                return;
            }

            handleResponse(mapping, request, response);
        } catch(ReponseException ee){
            String pageRed = ee.getPageRedirection();
            if (pageRed != null && !pageRed.isEmpty()) {
                RequestDispatcher rd =  request.getRequestDispatcher(pageRed);
                request.setAttribute("exception", ee);
                rd.forward(request, response);
            } else {
                response.sendError(ee.getStatusCode(), ee.getMessage());
            }
        } catch (Exception e) {
            renderErrorPage(response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request.getRequestURI()
            );
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected Gson  prepareGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.scannePackage(null);
            if (controleurs.size() == 0) {
                throw new ServletException("Pas de path trouver");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void renderErrorPage(HttpServletResponse response,
                             int statusCode,
                             String message,
                             String requestUri) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Erreur - Mon Application</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background:#f8f8f8; text-align:center; padding-top:50px; }");
            out.println(".error-box { background:#fff; border:1px solid #ddd; border-radius:12px; display:inline-block; padding:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);} ");
            out.println("h1 { color:#e74c3c; }");
            out.println(".code { font-size:22px; margin:15px 0; }");
            out.println(".message { color:#555; }");
            out.println("a { margin-top:20px; display:inline-block; text-decoration:none; color:#3498db; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='error-box'>");
            out.println("<h1>⚠️ Une erreur est survenue</h1>");
            out.println("<div class='code'>Code d'erreur : <strong>" + statusCode + "</strong></div>");
            out.println("<div class='message'>Message : " + (message != null ? message : "Erreur inconnue") + "</div>");
            out.println("<div class='uri'>URL demandée : " + (requestUri != null ? requestUri : "") + "</div>");
            out.println("<a href='" + response.encodeURL("/") + "'>⬅ Retour à l'accueil</a>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }



    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public static void main(String[] args) {
        System.err.println(Paths.get("gg","/","/").toUri().getPath());
    }

}
