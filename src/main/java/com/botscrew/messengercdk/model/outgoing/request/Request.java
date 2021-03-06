/*
 * Copyright 2018 BotsCrew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.botscrew.messengercdk.model.outgoing.request;

import com.botscrew.botframework.sender.Message;
import com.botscrew.messengercdk.model.incomming.UserInfo;

public abstract class Request implements Message {

    private UserInfo recipient;

    public Request() {
    }

    public Request(UserInfo recipient) {
        this.recipient = recipient;
    }

    public UserInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "Request{" +
                "recipient=" + recipient +
                '}';
    }
}