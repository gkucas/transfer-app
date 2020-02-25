package com.gedkua.transfer;

import com.gedkua.transfer.api.AccountServlet;
import com.gedkua.transfer.api.TransferServlet;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class TransferApplication {

  public static final EntityManagerFactory entityManagerFactory =
      Persistence.createEntityManagerFactory("com" +
      ".gedkua.transfer" +
      ".persistence");

  public static Server createServer() {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.setConnectors(new Connector[] {connector});

    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(TransferServlet.class, "/transfer");
    handler.addServletWithMapping(AccountServlet.class, "/account/*");
    server.setHandler(handler);

    return server;
  }

  public static void main(String[] args) throws Exception {
    Server server = createServer();
    server.start();
    server.join();
  }

}
