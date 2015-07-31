/*
 * Copyright 2015 Label305
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.label305.asynctask.example;

import com.label305.asynctask.TestAsyncTaskExecutor;
import java.io.IOException;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("NewExceptionWithoutArguments")
public class UserRetrieverTest {

  @Test
  public void retrieveUser_retrievesUser_andNotifiesCallback() throws IOException {
    /* Given */
    User user = new User();

    WebService webService = mock(WebService.class);
    when(webService.retrieveUser()).thenReturn(user);

    UserRetriever.UserCallback callback = mock(UserRetriever.UserCallback.class);

    UserRetriever userRetriever = new UserRetriever(webService, TestAsyncTaskExecutor.instance());

    /* When */
    userRetriever.retrieveUser(callback);

    /* Then */
    verify(callback).onUserRetrieved(user);
  }

  @Test
  public void retrieveUser_retrievesUser_withException_notifiesCallback() throws IOException {
    /* Given */
    IOException e = new IOException();

    WebService webService = mock(WebService.class);
    when(webService.retrieveUser()).thenThrow(e);

    UserRetriever.UserCallback callback = mock(UserRetriever.UserCallback.class);

    UserRetriever userRetriever = new UserRetriever(webService, TestAsyncTaskExecutor.instance());

    /* When */
    userRetriever.retrieveUser(callback);

    /* Then */
    verify(callback).onException(e);
  }
}