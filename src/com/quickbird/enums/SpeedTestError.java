package com.quickbird.enums;

public enum SpeedTestError {
    COULD_NOT_DOWNLOAD_SERVERS, PREPARING_CURRENT_TEST, STARTING_CURRENT_TEST, INIT_ENGINE, TEST_RUN, TEST_RUN_IO, TEST_CANCELLED, DEVICE_NOT_ONLINE, THROTTLING_LIMIT, None;

    public String getEnglishText() {
        String str;
        SpeedTestError error = SpeedTestError.None;
        switch (error)
        {
        default:
          str = "An error occurred. Please check your connection and try again.";
          break;
        case PREPARING_CURRENT_TEST:
          str = "Could not find closest server. Please check your connection and try again.";
          break;
        case THROTTLING_LIMIT:
          str = "Cannot run tests. The daily limit on the server has been reached.";
        }
        return str;
    }
}
