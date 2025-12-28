package com.example.cnsmsclient;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cnsmsclient.util.PrefsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PrefsManagerTest {

    private static final String FAKE_TOKEN = "fake.jwt.token";

    @Mock
    Context mockContext;

    @Mock
    SharedPreferences mockSharedPreferences;

    @Mock
    SharedPreferences.Editor mockEditor;

    private PrefsManager prefsManager;

    @Before
    public void setUp() {
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        prefsManager = new PrefsManager(mockContext);
    }

    @Test
    public void saveToken_savesTokenToPrefs() {
        prefsManager.saveToken(FAKE_TOKEN);
        verify(mockEditor).putString("auth_token", FAKE_TOKEN);
        verify(mockEditor).apply();
    }

    @Test
    public void getToken_retrievesSavedToken() {
        when(mockSharedPreferences.getString("auth_token", null)).thenReturn(FAKE_TOKEN);
        String token = prefsManager.getToken();
        assertEquals(FAKE_TOKEN, token);
    }
}
