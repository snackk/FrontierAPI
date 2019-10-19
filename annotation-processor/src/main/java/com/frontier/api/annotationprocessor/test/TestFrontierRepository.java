package com.frontier.api.annotationprocessor.test;

import com.frontier.api.annotationprocessor.provider.repository.FrontierProviderRepository;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

@FrontierProviderRepository
public interface TestFrontierRepository extends CrudRepository<User, Long> {

  public List<User> findAllByEmail(String email);

}
