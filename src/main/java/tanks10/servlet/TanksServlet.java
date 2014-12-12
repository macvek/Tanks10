/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks10.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tanks10.TanksServer;
import tanks10.world.TanksWorld;

/**
 *
 * @author macvek
 */
public class TanksServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        final StringBuilder builder = new StringBuilder();

        try (InputStream input = getServletContext().getResourceAsStream("index.html")) {
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.write(builder.toString());
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private TanksServer tanksServer;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        tanksServer = TanksServer.bootstrapServer(TANKS_SERVER_IP, TANKS_SERVER_PORT);
    }

    private final int TANKS_SERVER_PORT = 4444;
    private final String TANKS_SERVER_IP = "localhost";

    @Override
    public void destroy() {
        tanksServer.stop();
        TanksWorld.end();
    }

}
