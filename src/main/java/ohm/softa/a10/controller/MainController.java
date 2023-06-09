package ohm.softa.a10.controller;

import ohm.softa.a10.internals.displaying.ProgressReporter;
import ohm.softa.a10.kitchen.KitchenHatch;
import ohm.softa.a10.kitchen.KitchenHatchImpl;
import ohm.softa.a10.kitchen.workers.Cook;
import ohm.softa.a10.kitchen.workers.Waiter;
import ohm.softa.a10.util.NameGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ResourceBundle;

import static ohm.softa.a10.KitchenHatchConstants.*;

public class MainController implements Initializable {

	private final ProgressReporter progressReporter;
	private final KitchenHatch kitchenHatch;
	private final NameGenerator nameGenerator;

	@FXML
	private ProgressIndicator waitersBusyIndicator;

	@FXML
	private ProgressIndicator cooksBusyIndicator;

	@FXML
	private ProgressBar kitchenHatchProgress;

	@FXML
	private ProgressBar orderQueueProgress;

	public MainController() {
		nameGenerator = new NameGenerator();

		//TODO assign an instance of your implementation of the KitchenHatch interface
		this.kitchenHatch = new KitchenHatchImpl(KITCHEN_HATCH_SIZE,ORDER_COUNT, nameGenerator);
		this.progressReporter = new ProgressReporter(kitchenHatch, COOKS_COUNT, WAITERS_COUNT, ORDER_COUNT, KITCHEN_HATCH_SIZE);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		orderQueueProgress.progressProperty().bindBidirectional(this.progressReporter.orderQueueProgressProperty());
		kitchenHatchProgress.progressProperty().bindBidirectional(this.progressReporter.kitchenHatchProgressProperty());
		waitersBusyIndicator.progressProperty().bindBidirectional(this.progressReporter.waitersBusyProperty());
		cooksBusyIndicator.progressProperty().bind(this.progressReporter.cooksBusyProperty());

		/* TODO create the cooks and waiters, pass the kitchen hatch and the reporter instance and start them */

		spawnAndStart(COOKS_COUNT,"cook");
		spawnAndStart(WAITERS_COUNT,"waiter");
		//spawnAndStart(1,"cook");
		//spawnAndStart(1,"waiter");


	}

	private void spawnAndStart(int count, String s){
		for(int i=0;i<count;i++){
			if(s=="waiter")
				new Thread(new Waiter(nameGenerator.generateName(),kitchenHatch,progressReporter))
					.start();
			else if(s=="cook")
				new Thread(new Cook(nameGenerator.generateName(),kitchenHatch,progressReporter))
					.start();
			else
				throw new IllegalStateException("No Valid Object found");
		}
	}
}
