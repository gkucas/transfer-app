package com.gedkua.transfer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ResponseWriter implements Serializable {

  private final ObjectMapper objectMapper = new ObjectMapper();

  void writeResponse(Object object,
      HttpServletResponse response,
      ServletOutputStream out) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writeValue(out, object);
  }

  void writeResponse(HttpServletResponse response,
      ServletOutputStream out) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
  }

  void writeError(Exception exception,
      HttpServletResponse response,
      ServletOutputStream out) throws IOException {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    objectMapper.writeValue(out, exception.getMessage());
  }
}