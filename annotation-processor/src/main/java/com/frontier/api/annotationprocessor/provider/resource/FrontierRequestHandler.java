package com.frontier.api.annotationprocessor.provider.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FrontierRequestHandler {

  private final CrudRepository crudRepository;

  public FrontierRequestHandler(CrudRepository crudRepository) {
    this.crudRepository = crudRepository;
  }

  public ResponseEntity<Object> handleRequest(String method, HttpServletRequest request,
      HttpServletResponse response, Object body) {
    // TODO Auto-generated method stub
    switch (method) {
      case "GET":
        return findAll();
      default:
        return new ResponseEntity<Object>("", HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

  private ResponseEntity<Object> save(Object body) {
    return ResponseEntity.ok(this.crudRepository.save(body));
  }

  private ResponseEntity<Object> saveAll(Iterable iterable) {
    return ResponseEntity.ok(this.crudRepository.saveAll(iterable));
  }

  private ResponseEntity<Object> findById(Object body) {
    return ResponseEntity.ok(this.crudRepository.findById(body));
  }

  private ResponseEntity<Object> existsById(Object body) {
    return ResponseEntity.ok(this.crudRepository.existsById(body));
  }

  private ResponseEntity<Object> findAll() {
    return ResponseEntity.ok(this.crudRepository.findAll());
  }

  private ResponseEntity<Object> findAllById(Iterable iterable) {
    return ResponseEntity.ok(this.crudRepository.findAllById(iterable));
  }

  private ResponseEntity<Object> count() {
    return ResponseEntity.ok(this.crudRepository.count());
  }

  private ResponseEntity<Object> deleteById(Object body) {
    this.crudRepository.deleteById(body);
    return ResponseEntity.ok("");
  }

  private ResponseEntity<Object> delete(Object body) {
    this.crudRepository.delete(body);
    return ResponseEntity.ok("");
  }

  private ResponseEntity<Object> deleteAll() {
    this.crudRepository.deleteAll();
    return ResponseEntity.ok("");
  }

  private ResponseEntity<Object> deleteAll(Iterable iterable) {
    this.crudRepository.deleteAll(iterable);
    return ResponseEntity.ok("");
  }
}
