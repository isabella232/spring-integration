/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.mail;

import javax.mail.Message;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Oleg Zhurakousky
 *
 */
public class AbstractMailReceiverTests {

	@Test(expected=IllegalArgumentException.class)
	public void validateDeleteAndReadIsNotAllowed(){
		AbstractMailReceiver receiver = new ImapMailReceiver();
		receiver.setShouldDeleteMessages(true);
		receiver.setShouldMarkMessagesAsRead(true);
	}
	@Test
	public void validateDeleteOrReadIsAllowed_Read(){
		AbstractMailReceiver receiver = new ImapMailReceiver();
		receiver.setShouldDeleteMessages(false);
		receiver.setShouldMarkMessagesAsRead(true);
	}
	@Test
	public void validateDeleteOrReadIsAllowed_Delete(){
		AbstractMailReceiver receiver = new ImapMailReceiver();
		receiver.setShouldDeleteMessages(true);
		receiver.setShouldMarkMessagesAsRead(false);
	}
	
	@Test
	public void receieveAndMarkAsRead() throws Exception{
		AbstractMailReceiver receiver = new ImapMailReceiver();
		receiver.setShouldMarkMessagesAsRead(true);
		receiver = spy(receiver);
		Message msg1 = mock(MimeMessage.class);
		Message msg2 = mock(MimeMessage.class);
		final Message[] messages = new Message[]{msg1, msg2};
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				// just to avoid the exception
				return null;
			}
		}).when(receiver).openFolder();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return messages;
			}
		}).when(receiver).searchForNewMessages();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(receiver).fetchMessages(messages);
		receiver.receive();
		verify(msg1, times(1)).setFlag(Flag.SEEN, true);
		verify(msg2, times(1)).setFlag(Flag.SEEN, true);
	}
	@Test
	public void receieveAndDontMarkAsRead() throws Exception{
		AbstractMailReceiver receiver = new ImapMailReceiver();
		receiver = spy(receiver);
		Message msg1 = mock(MimeMessage.class);
		Message msg2 = mock(MimeMessage.class);
		final Message[] messages = new Message[]{msg1, msg2};
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				// just to avoid the exception
				return null;
			}
		}).when(receiver).openFolder();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return messages;
			}
		}).when(receiver).searchForNewMessages();
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(receiver).fetchMessages(messages);
		receiver.receive();
		verify(msg1, times(0)).setFlag(Flag.SEEN, true);
		verify(msg2, times(0)).setFlag(Flag.SEEN, true);
	}
}
