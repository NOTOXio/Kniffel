module dev.dhbwloerrach.kamiio.kniffel {
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.graphics;

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
  opens dev.dhbwloerrach.kamiio.kniffel to javafx.fxml;

  exports dev.dhbwloerrach.kamiio.kniffel.app;
  exports dev.dhbwloerrach.kamiio.kniffel.controller;
  exports dev.dhbwloerrach.kamiio.kniffel.model;
  exports dev.dhbwloerrach.kamiio.kniffel.ui;
  exports dev.dhbwloerrach.kamiio.kniffel.game;
  exports dev.dhbwloerrach.kamiio.kniffel.utils;
  exports dev.dhbwloerrach.kamiio.kniffel;
}