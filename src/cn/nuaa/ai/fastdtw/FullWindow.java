package cn.nuaa.ai.fastdtw;

import cn.nuaa.ai.fastdtw.TimeSeries;

public class FullWindow extends SearchWindow {

	// CONSTRUCTOR
	public FullWindow(TimeSeries tsI, TimeSeries tsJ) {
		super(tsI.size(), tsJ.size());

		for (int i = 0; i < tsI.size(); i++) {
			super.markVisited(i, minJ());
			super.markVisited(i, maxJ());
		} // end for loop
	} // end CONSTRUCTOR

} // end class FullWindow
