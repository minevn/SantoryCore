package mk.plugin.santory.traveler;

public class Traveler {
	
	private TravelerState state;
	private TravelerData data;
	
	public Traveler() {}
	
	public Traveler(TravelerData data) {
		this.state = new TravelerState();
		this.data = data;
	}
	
	public Traveler(TravelerState state, TravelerData data) {
		this.state = state;
		this.data = data;
	}
	
	public TravelerState getState() {
		return this.state;
	}
	
	public TravelerData getData() {
		return this.data;
	}

	
}
