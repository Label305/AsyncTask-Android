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

package com.label305.asynctask;

import android.support.annotation.NonNull;

/**
 * A delegate interface that can execute AsyncTasks.
 *
 * @author Niek Haarman <niek@label305.com>
 */
public interface AsyncTaskExecutor {

  AsyncTaskExecutor DEFAULT_EXECUTOR = new DefaultAsyncTaskExecutor();

  <T, E extends Exception, A extends AsyncTask<T, E>> A execute(@NonNull A task);
}
