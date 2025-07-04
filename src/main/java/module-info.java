module dev.dhbwloerrach.kamiio.kniffel {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;

  opens dev.dhbwloerrach.kamiio.kniffel.app to javafx.fxml;
  opens dev.dhbwloerrach.kamiio.kniffel.controller to javafx.fxml;
  opens dev.dhbwloerrach.kamiio.kniffel.model to javafx.fxml;
  opens dev.dhbwloerrach.kamiio.kniffel.ui to javafx.fxml;
  opens dev.dhbwloerrach.kamiio.kniffel.game to javafx.fxml;
  opens dev.dhbwloerrach.kamiio.kniffel.utils to javafx.fxml;
  
  exports dev.dhbwloerrach.kamiio.kniffel.app;
  exports dev.dhbwloerrach.kamiio.kniffel.controller;
  exports dev.dhbwloerrach.kamiio.kniffel.model;
  exports dev.dhbwloerrach.kamiio.kniffel.ui;
  exports dev.dhbwloerrach.kamiio.kniffel.game;
  exports dev.dhbwloerrach.kamiio.kniffel.utils;
}