package com.github.santosleijon.users;

import com.github.santosleijon.common.EnvironmentVariableReader;
import org.mockito.Mockito;

public class EnvironmentVariableReaderMock extends EnvironmentVariableReader {

    @Override
    public String getDbHost() {
        return "N/A";
    }

    @Override
    public Integer getDbPort() {
        return 0;
    }

    @Override
    public String getDbUser() {
        return "N/A";
    }

    @Override
    public String getDbPassword() {
        return "N/A";
    }

    public void setupMock(EnvironmentVariableReader mockedEnvironmentVariableReader) {
        Mockito.when(mockedEnvironmentVariableReader.getDbHost()).thenReturn(getDbHost());
        Mockito.when(mockedEnvironmentVariableReader.getDbPort()).thenReturn(getDbPort());
        Mockito.when(mockedEnvironmentVariableReader.getDbUser()).thenReturn(getDbHost());
        Mockito.when(mockedEnvironmentVariableReader.getDbPassword()).thenReturn(getDbPassword());
    }
}
