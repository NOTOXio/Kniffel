module dev.dhbwloerrach.kamiio.kniffel {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;

  opens dev.dhbwloerrach.kamiio.kniffel to javafx.fxml;
  exports dev.dhbwloerrach.kamiio.kniffel;
}