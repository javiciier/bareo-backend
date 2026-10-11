/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/** Configuration class to initialize Firebase services. */
@Slf4j
@Configuration
public class FirebaseConfiguration {

  private static final String FIREBASE_CREDENTIALS_FILE_NAME = "firebase.json";

  private final ResourceLoader resourceLoader;

  @Value("${spring.application.name}")
  private String APPLICATION_NAME;

  public FirebaseConfiguration(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  private FileInputStream readFirebaseCredentials() {
    try {
      File credentialsFile = new File("firebase.json");
      Resource firebaseJsonResource =
          resourceLoader.getResource("file:" + FIREBASE_CREDENTIALS_FILE_NAME);
      if (!firebaseJsonResource.exists()) {
        throw new FileNotFoundException(FIREBASE_CREDENTIALS_FILE_NAME);
      }

      return new FileInputStream(credentialsFile);
    } catch (FileNotFoundException e) {
      logger.error("Could not read Firebase credentials file");
      throw new RuntimeException(e);
    }
  }

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    FirebaseOptions.Builder firebaseOptionsBuilder = FirebaseOptions.builder();

    Optional<FirebaseApp> existingApp =
        FirebaseApp.getApps().stream()
            .filter(app -> app.getName().equals(APPLICATION_NAME))
            .findFirst();
    if (existingApp.isPresent()) {
      logger.debug("Firebase App '{}' already initialized.", APPLICATION_NAME);
      return existingApp.get();
    }

    // Initialize app
    try (FileInputStream stream = readFirebaseCredentials()) {
      firebaseOptionsBuilder.setCredentials(GoogleCredentials.fromStream(stream));
    }

    FirebaseOptions firebaseOptions = firebaseOptionsBuilder.build();
    FirebaseApp initializedApp = FirebaseApp.initializeApp(firebaseOptions, APPLICATION_NAME);
    logger.debug("Firebase App '{}' initialized", initializedApp.getName());

    return initializedApp;
  }

  /** Initialize the {@link FirebaseAuth}. */
  @Bean
  public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    logger.debug("Firebase Auth for application '{}' initialized", firebaseApp.getName());

    return firebaseAuth;
  }

  /** Initialize the {@link Firestore} database. */
  @Bean
  public Firestore firestore(FirebaseApp firebaseApp) {
    Firestore firestore = FirestoreClient.getFirestore(firebaseApp);
    logger.debug("Firebase Firestore for application '{}' initialized", firebaseApp.getName());

    return firestore;
  }

  /** Initialize the {@link StorageClient} */
  @Bean
  public StorageClient storageClient(FirebaseApp firebaseApp) {
    StorageClient client = StorageClient.getInstance(firebaseApp);
    logger.debug("Firebase Storage client for application '{}' initialized", firebaseApp.getName());

    return client;
  }
}
