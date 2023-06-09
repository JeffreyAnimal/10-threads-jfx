package ohm.softa.a10.kitchen;

import ohm.softa.a10.model.Dish;
import ohm.softa.a10.model.Order;
import ohm.softa.a10.util.NameGenerator;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class KitchenHatchImpl implements KitchenHatch{

	private final int maxDishes;
	private int orderCount;
	private final NameGenerator nameGenerator;
	private Deque<Dish> dishes;

	public KitchenHatchImpl(int maxDishes, int orderCount, NameGenerator nameGenerator){
		this.maxDishes=maxDishes;
		this.dishes=new LinkedList<>();
		this.nameGenerator=nameGenerator;
		this.orderCount=orderCount;
	}

	@Override
	public int getMaxDishes() {
		return maxDishes;
	}

	@Override
	public Order dequeueOrder() {
		synchronized (this) {
			while (orderCount <= 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		orderCount--;
		this.notifyAll();
		}
		return new Order(nameGenerator.getRandomDish());
	}

	@Override
	public Order dequeueOrder(long timeout)  {
		synchronized (this) {
			while (getOrderCount() <= 0) {
				try {
					this.wait(timeout);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		orderCount--;
		this.notifyAll();
		return new Order(nameGenerator.getRandomDish());
	}

	@Override
	public int getOrderCount() {
		synchronized (this){
			return this.orderCount;
		}
	}

	@Override
	public Dish dequeueDish() {
		synchronized (dishes) {
			while (dishes.isEmpty()) {
				try {
					dishes.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		Dish d=dishes.removeLast();
		notifyAll();
		return d;
	}

	@Override
	public Dish dequeueDish(long timeout) {
		Dish d=null;
		synchronized (dishes) {
			while (dishes.isEmpty()) {
				try {
					dishes.wait(timeout);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

			d = dishes.removeLast();
			dishes.notifyAll();
		}
		return d;
	}

	@Override
	public void enqueueDish(Dish d){
		while(getDishesCount()>=maxDishes){
			try {
				dishes.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		synchronized (dishes){

			dishes.addFirst(d);
			dishes.notifyAll();
		}
	}

	@Override
	synchronized public int getDishesCount() {
		synchronized (dishes){
			return dishes.size();
		}
	}
}
