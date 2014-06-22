package cl.uai.webcursos.emarking.desktop.data;

import java.util.EventObject;

/**
 * 
 * @author jorgevillalon
 *
 */
public class MoodleWorkerEvent extends EventObject {

	private int current;
	private int total;
	public int getTotal() {
		return total;
	}

	public Object getOutput() {
		return output;
	}

	private Object output;
	
	public int getCurrent() {
		return current;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7606489359709233942L;
	
	public MoodleWorkerEvent(Object source, int current, int total, Object output) {
		super(source);
		this.current = current;
		this.total = total;
		this.output = output;
	}
}
