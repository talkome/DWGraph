package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CL_Agent {
	public static final double EPS = 0.0001;
	private static int _count = 0;
	private static int _seed = 3331;
	private int id;
	//	private long _key;
	private geo_location pos;
	private double speed;
	private edge_data currEdge;
	private node_data currNode;
	private directed_weighted_graph graph;
	private CL_Pokemon currPokemon;
	private long _sg_dt;
	private double value;
	private String idLocation;
	private List<CL_Pokemon> targetPokemonsList;
	private String pic;

	private boolean _chasing;
		
	public CL_Agent(directed_weighted_graph g, int start_node) {
		graph = g;
		setMoney(0);
		this.currNode = graph.getNode(start_node);
		pos = currNode.getLocation();
		id = -1;
		setSpeed(0);
		_chasing = false;
		pic = "resources/agent.png";
		idLocation = Double.toString(this.getLocation().x() + this.getLocation().y());
		this.targetPokemonsList = new ArrayList<>();
	}

	/*HELPFUL TOOLS*/
	public void updateTargetPokemonsList(CL_Pokemon pokemon) {
		List<CL_Pokemon> tempTargetPokemonsList = new ArrayList<>();
		for (CL_Pokemon currPokemon : getTargetPokemonsList()) {
			tempTargetPokemonsList.add(currPokemon);
		}
		tempTargetPokemonsList.add(pokemon);
		this.setTargetPokemonsList(tempTargetPokemonsList);
	}

	public boolean isMoving() {
		return this.currEdge !=null;
	}

	public boolean setNextNode(int dest) {
		boolean ans = false;
		int src = this.currNode.getKey();
		this.currEdge = graph.getEdge(src, dest);
		if(currEdge !=null)
			ans = true;
		else
			currEdge = null;
		return ans;
	}

	public void update(String json) {
			JSONObject line;
			try {
				// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
				line = new JSONObject(json);
				JSONObject ttt = line.getJSONObject("Agent");
				int id = ttt.getInt("id");
				if(id==this.getID() || this.getID() == -1) {
					if(this.getID() == -1) {
						this.id = id;}
					double speed = ttt.getDouble("speed");
					String p = ttt.getString("pos");
					Point3D pp = new Point3D(p);
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					double value = ttt.getDouble("value");
					this.pos = pp;
					this.setCurrNode(src);
					this.setSpeed(speed);
					this.setNextNode(dest);
					this.setMoney(value);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

	public String toJSON() {
		int d = this.getNextNode();
		String ans = "{\"Agent\":{"
				+ "\"id\":"+this.id +","
				+ "\"value\":"+this.value +","
				+ "\"src\":"+this.currNode.getKey()+","
				+ "\"dest\":"+d+","
				+ "\"speed\":"+this.getSpeed()+","
				+ "\"pos\":\""+ pos.toString()+"\""
				+ "}"
				+ "}";
		return ans;
	}

	public String toString() {
		return toJSON();
	}

	public String toString1() {
		return ""+ this.getID()+","+ pos +", "+isMoving()+","+ this.getValue();
	}

	/*GETS & SETS*/
	public List<CL_Pokemon> getTargetPokemonsList() {
		return targetPokemonsList;
	}

	public void setTargetPokemonsList(List<CL_Pokemon> targetPokemonsList) {
		this.targetPokemonsList = targetPokemonsList;
	}

	public int getId() {
		return id;
	}

	public int getSrcNode() {return this.currNode.getKey();}

	private void setMoney(double v) {
		value = v;
	}

	public void setCurrNode(int src) {
		this.currNode = graph.getNode(src);
	}

	public int getID() {
		return this.id;
	}
	
	public geo_location getLocation() {
		return pos;
	}

	public String getPic() {
		return pic;
	}
		
	public double getValue() {
		return this.value;
	}

	public int getNextNode() {
		int ans = -2;
		if(currEdge == null)
			ans = -1;
		else
			ans = this.currEdge.getDest();
		return ans;
	}

	public double getSpeed() {
		return this.speed;
	}

	public void setSpeed(double v) {
		this.speed = v;
	}

	public CL_Pokemon getCurrPokemon() {
		return currPokemon;
	}

	public void setCurrPokemon(CL_Pokemon curr_fruit) {
		this.currPokemon = curr_fruit;
	}

	public void set_SDT(long ddtt) {
			long ddt = ddtt;
			if(this.currEdge !=null) {
				double w = getCurrEdge().getWeight();
				geo_location dest = graph.getNode(getCurrEdge().getDest()).getLocation();
				geo_location src = graph.getNode(getCurrEdge().getSrc()).getLocation();
				double de = src.distance(dest);
				double dist = pos.distance(dest);
				if(this.getCurrPokemon().getEdge()==this.getCurrEdge()) {
					 dist = currPokemon.getLocation().distance(this.pos);
				}
				double norm = dist/de;
				double dt = w*norm / this.getSpeed(); 
				ddt = (long)(1000.0*dt);
			}
			this.set_sg_dt(ddt);
		}
		
	public edge_data getCurrEdge() {
		return this.currEdge;
	}

	public long get_sg_dt() {
		return _sg_dt;
	}

	public void set_sg_dt(long _sg_dt) {
		this._sg_dt = _sg_dt;
	}
}
