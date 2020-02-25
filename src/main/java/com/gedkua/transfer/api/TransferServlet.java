package com.gedkua.transfer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gedkua.transfer.TransferApplication;
import com.gedkua.transfer.persistence.AccountRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.MimeTypes;

public class TransferServlet extends HttpServlet {

  private final ObjectMapper objectMapper;
  private final AccountRepository accountRepository;
  private final ResponseWriter responseWriter = new ResponseWriter();

  public TransferServlet() {
    this.accountRepository = new AccountRepository(TransferApplication.entityManagerFactory);
    this.objectMapper = new ObjectMapper();
  }

  @Override
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    AsyncContext async = request.startAsync();
    if (!MimeTypes.Type.APPLICATION_JSON.asString().equals(request.getContentType())) {
      response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
      async.complete();
    } else {
      TransferRequest transferRequest = objectMapper.readValue(request.getReader(),
          TransferRequest.class);
      Optional<TransferException> exception = accountRepository.transfer(transferRequest);
      ServletOutputStream out = response.getOutputStream();
      out.setWriteListener(new WriteListener() {
        @Override
        public void onWritePossible() throws IOException {
          try {
            if (exception.isPresent()) {
              responseWriter.writeError(exception.get(), response, out);
            } else {
              responseWriter.writeResponse(response, out);
            }
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