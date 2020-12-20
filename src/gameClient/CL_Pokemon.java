package gameClient;

import api.edge_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * this class represent the pokemon features
 */
public class CL_Pokemon {
	private edge_data edge;
	private double value;
	private int type;
	private Point3D pos;
	private double minDist;
	private int minRo;
	
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		type = t;
	//	_speed = s;
		value = v;
		setEdge(e);
		pos = p;
		minDist = -1;
		minRo = -1;
	}

	/*HELPFUL TOOLS*/
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	public String toString() {return "F:{v="+ value +", t="+ type +"}";}

	/*GETS & SETS*/
	public edge_data getEdge() {
		return edge;
	}

	public void setEdge(edge_data edge) {
		this.edge = edge;
	}

	public Point3D getLocation() {
		return pos;
	}

	public int getType() {return type;}

//	public double getSpeed() {return _speed;}

	public double getValue() {return value;}

	public void setValue(double value) {
		this.value = value;
	}

	public double getMinDist() {
		return minDist;
	}

	public void setMinDist(double mid_dist) {
		this.minDist = mid_dist;
	}

	public int getMinRo() {
		return minRo;
	}

	public void setMinRo(int minRo) {
		this.minRo = minRo;
	}
}
