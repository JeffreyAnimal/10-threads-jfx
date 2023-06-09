package ohm.softa.a10.kitchen.workers;

import ohm.softa.a10.internals.displaying.ProgressReporter;
import ohm.softa.a10.kitchen.KitchenHatch;
import ohm.softa.a10.model.Dish;


public class Cook implements Runnable{
	private String name;
	private ProgressReporter progressReporter;
	private KitchenHatch kitchenHatch;

	public Cook(String name, KitchenHatch kitchenHatch, ProgressReporter progressReporter){
		this.name=name;
		this.kitchenHatch=kitchenHatch;
		this.progressReporter=progressReporter;
	}

	@Override
	synchronized public void run(){
		Dish dish;

		while(kitchenHatch.getOrderCount()>0){
			while(kitchenHatch.getDishesCount()>=kitchenHatch.getMaxDishes()) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			dish=new Dish(kitchenHatch.dequeueOrder().getMealName());
			notifyAll();

			try {
				Thread.sleep(dish.getCookingTime());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			kitchenHatch.enqueueDish(dish);
			notifyAll();

			progressReporter.updateProgress();
		}

		progressReporter.notifyCookLeaving();
	}
}
