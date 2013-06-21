package com.quickbird.speedtest.business;

import java.net.URL;

public interface MulDownload {

	public void mulDownload(DownloadProgressListener downloadProgressListener,
			URL url, int threadNums);

}
