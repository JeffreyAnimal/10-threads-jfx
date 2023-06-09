package ohm.softa.a10.kitchen.workers;

import ohm.softa.a10.internals.displaying.ProgressReporter;
import ohm.softa.a10.kitchen.KitchenHatch;
import ohm.softa.a10.model.Dish;

import java.util.Random;

public class Waiter implements Runnable{
	private String name;
	private ProgressReporter progressReporter;
	private KitchenHatch kitchenHatch;
	private Random random;

	public Waiter(String name, KitchenHatch kitchenHatch, ProgressReporter progressReporter){
		this.name=name;
		this.kitchenHatch=kitchenHatch;
		this.progressReporter=progressReporter;
		this.random=new Random();
	}

	@Override
	synchronized public void run() {
		while(kitchenHatch.getOrderCount()>0||
			kitchenHatch.getDishesCount()>0){

				while (kitchenHatch.getDishesCount() <= 0) {
					try {
						wait(5000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
			}
			kitchenHatch.dequeueDish(random.nextInt(1000));
			progressReporter.updateProgress();
			notifyAll();
		}

		progressReporter.notifyWaiterLeaving();
	}
}
