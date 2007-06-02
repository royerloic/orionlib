package utils.optimal.gui;

/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public interface IOptimalEngineControl
{

	/**
	 * Starts the OptimalEngine.
	 */
	void doStart();

	/**
	 * Pauses the OptimalEngine.
	 */
	void doPause();

	/**
	 * Resumes the OptimalEngine.
	 */
	void doResume();

	/**
	 * Stops the OptimalEngine.
	 */
	void doStop();

	/**
	 * Gives information on the state of the OptimalEngine.
	 * 
	 * @return true if the OptimalEngine is paused
	 */
	boolean isPaused();

	/**
	 * Gives information on the state of the OptimalEngine.
	 * 
	 * @return true if the OptimalEngine is started
	 */
	boolean isStarted();
}