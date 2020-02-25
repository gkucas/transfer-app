package com.gedkua.transfer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gedkua.transfer.TransferApplication;
import com.gedkua.transfer.persistence.Account;
import com.gedkua.transfer.persistence.AccountRepository;
import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.MimeTypes;

public class AccountServlet extends HttpServlet {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AccountRepository accountRepository =
      new AccountRepository(TransferApplication.entityManagerFactory);
  private final ResponseWriter responseWriter = new ResponseWriter();


  @Override
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    AsyncContext async = request.startAsync();
    if (!MimeTypes.Type.APPLICATION_JSON.asString().equals(request.getContentType())) {
      response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      async.complete();
    } else {
      Account account = accountRepository.create(objectMapper.readValue(request.getReader(),
          Account.class));
      ServletOutputStream out = response.getOutputStream();
      out.setWriteListener(new WriteListener() {
        @Override
        public void onWritePossible() throws IOException {
          try {
            responseWriter.writeResponse(account, response, out);
          } catch (Exception e) {
            responseWriter.writeError(e, response, out);
          }
          async.complete();
        }

        @Override
        public void onError(Throwable t) {
          getServletContext().log("Error", t);
          async.complete();
        }
      });
    }
  }

  @Override
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    AsyncContext async = request.startAsync();
    if (!MimeTypes.Type.APPLICATION_JSON.asString().equals(request.getContentType())) {
      response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      async.complete();
    } else {
      Account account =
          accountRepository.findById(Long.valueOf(request.getPathInfo().split("/account/")[1]));
      ServletOutputStream out = response.getOutputStream();
      out.setWriteListener(new WriteListener() {
        @Override
        public void onWritePossible() throws IOException {
          try {
            responseWriter.writeResponse(account, response, out);
          } catch (Exception e) {
            responseWriter.writeError(e, response, out);
          }
          async.complete();
        }

        @Override
        public void onError(Throwable t) {
          getServletContext().log("Error", t);
          async.complete();
        }
      });
    }
  }

}
