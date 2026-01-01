package com.example.cnsmsclient;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cnsmsclient.util.PrefsManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PrefsManagerTest {

    private static final String FAKE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Mock
    Context mockContext;

    @Mock
    SharedPreferences mockPrefs;

    @Mock
    SharedPreferences.Editor mockEditor;

    private PrefsManager prefsManager;

    @Before
    public void setup() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        prefsManager = new PrefsManager(mockContext);
    }

    @Test
    public void saveToken_storesTokenCorrectly() {
        prefsManager.saveToken(FAKE_TOKEN);
        verify(mockEditor).putString(eq("auth_token"), eq(FAKE_TOKEN));
        verify(mockEditor).apply();
    }

    @Test
    public void getToken_retrievesCorrectToken() {
        when(mockPrefs.getString(eq("auth_token"), isNull())).thenReturn(FAKE_TOKEN);
        String token = prefsManager.getToken();
        assertEquals(FAKE_TOKEN, token);
    }
}
