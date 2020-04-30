package com.idemia.jkt.tec.VerifClient;

import java.io.IOException;

import com.idemia.jkt.tec.VerifClient.controller.*;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SpringBootApplication
public class VerifClientApplication extends Application {
	
	private ConfigurableApplicationContext springContext;
	
	static Logger logger = Logger.getLogger(VerifClientApplication.class.getName());
	
	private BorderPane rootLayout;
	private Stage primaryStage;
	private Stage editLiteralsDialogStage;
	private Stage selectReaderDialogStage;
	private Stage customApduDialogStage;
	private Stage createScriptStage;
	private Stage userGuideStage;
	private Stage aboutDialogStage;

	public static void main(String[] args) {
		launch(VerifClientApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("VerifClient");
		
		initRootLayout();
		showVerifClient();
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(VerifClientApplication.class);
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RootLayout.fxml"));
			loader.setControllerFactory(springContext::getBean);
			rootLayout = loader.load();
						
			// give controller access to main app
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			Scene scene = new Scene(rootLayout);
			
			rootLayout.requestFocus();
			
			primaryStage.setScene(scene);			
			primaryStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/fv_icon.png")));
			primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showVerifClient() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VerifClient.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane verifClient = loader.load();
			
			rootLayout.setCenter(verifClient);
			
			// give controller access to main app
			VerifClientController controller = loader.getController();
			controller.setMainApp(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showEditLiterals() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditLiterals.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane editLiterals = loader.load();
			
			// give controller access to main app
			EditLiteralsController controller = loader.getController();
			controller.setMainApp(this);
			
			// create dialog
			editLiteralsDialogStage = new Stage();
			editLiteralsDialogStage.setTitle("Edit Literals");
			editLiteralsDialogStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_format_shapes_white_18dp.png")));
			editLiteralsDialogStage.setResizable(false);
			editLiteralsDialogStage.initModality(Modality.WINDOW_MODAL);
			editLiteralsDialogStage.initOwner(primaryStage);
			Scene scene = new Scene(editLiterals);
			editLiteralsDialogStage.setScene(scene);
			
			editLiteralsDialogStage.showAndWait();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showSelectReader() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectReader.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane selectReader = loader.load();
			
			// give controller access to main app
			SelectReaderController controller = loader.getController();
			controller.setMainApp(this);
			
			// create dialog
			selectReaderDialogStage = new Stage();
			selectReaderDialogStage.setTitle("Select Reader");
			selectReaderDialogStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_usb_white_18dp.png")));
			selectReaderDialogStage.setResizable(false);
			selectReaderDialogStage.initModality(Modality.WINDOW_MODAL);
			selectReaderDialogStage.initOwner(primaryStage);
			Scene scene = new Scene(selectReader);
			selectReaderDialogStage.setScene(scene);
			
			selectReaderDialogStage.showAndWait();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showCustomApdu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomAPDU.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane customApdu = loader.load();
			
			// give controller access to main app
			CustomAPDUController controller = loader.getController();
			controller.setMainApp(this);
			
			// create dialog
			customApduDialogStage = new Stage();
			customApduDialogStage.setTitle("Custom APDU");
			customApduDialogStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_settings_white_18dp.png")));
			customApduDialogStage.setResizable(false);
			customApduDialogStage.initModality(Modality.WINDOW_MODAL);
			customApduDialogStage.initOwner(primaryStage);
			Scene scene = new Scene(customApdu);
			customApduDialogStage.setScene(scene);
			
			customApduDialogStage.showAndWait();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showGenerateScript() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateScript.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane createScript = loader.load();

			// give controller access to main app
			CreateScriptController controller = loader.getController();
			controller.setMainApp(this);

			// create dialog
			createScriptStage = new Stage();
			createScriptStage.setTitle("Generate non-regression script");
			createScriptStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_notes_white_18dp.png")));
			createScriptStage.setResizable(false);
			createScriptStage.initModality(Modality.WINDOW_MODAL);
			createScriptStage.initOwner(primaryStage);
			Scene scene = new Scene(createScript);
			createScriptStage.setScene(scene);

			createScriptStage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showUserGuide() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserGuide.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane userGuide = loader.load();
			
			// give controller access to main app
			UserGuideController controller = loader.getController();
			controller.setMainApp(this);
			
			// create dialog
			userGuideStage = new Stage();
			userGuideStage.setTitle("User Guide");
			userGuideStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_menu_book_white_18dp.png")));
			userGuideStage.initModality(Modality.WINDOW_MODAL);
			userGuideStage.initOwner(primaryStage);
			Scene scene = new Scene(userGuide);
			userGuideStage.setScene(scene);
			
			userGuideStage.showAndWait();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showAbout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/About.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane about = loader.load();
			
			// give controller access to main app
			AboutController controller = loader.getController();
			controller.setMainApp(this);
			
			// create dialog
			aboutDialogStage = new Stage();
			aboutDialogStage.setTitle("About");
			aboutDialogStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/fv_icon.png")));
			
			aboutDialogStage.setResizable(false);
			aboutDialogStage.initModality(Modality.WINDOW_MODAL);
			aboutDialogStage.initOwner(primaryStage);
			Scene scene = new Scene(about);
			aboutDialogStage.setScene(scene);
			
			aboutDialogStage.showAndWait();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Stage getEditLiteralsDialogStage() {
		return editLiteralsDialogStage;
	}

	public Stage getSelectReaderDialogStage() {
		return selectReaderDialogStage;
	}
	

	public Stage getCustomApduDialogStage() {
		return customApduDialogStage;
	}

	public Stage getAboutDialogStage() {
		return aboutDialogStage;
	}
	
}
