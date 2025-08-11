import { Client, Frame, IMessage } from '@stomp/stompjs';
import WebSocket from 'ws';

const WS_URL = 'ws://localhost:8080/ws/thing';
const JWT_TOKEN = 'eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJidWxsZXRpbiIsInN1YiI6IjQiLCJleHAiOjE3NTY1ODkzOTQsImlhdCI6MTc1Mzk5NzM5NH0.E_ECKse5r4ArxwuKcUILK8nUzOpSwJaEs4IgZQeGdUwfGzvXadbTguIa2NwHeyuOatxfG8DzDTBHQdoMUgITAdULaywKW6qMIz2VFTJ77s0oj2BGF7L1QpkwbB4AgqRmcs2G8S-ZsZG36146bOlSGIgNaCU2mWvxs-FW1Md2b8BZxzQ-I6ytBiWznKE_uC1xqyGQhGZ5KAORiW4ssBwtBlX_QFYHppd_kJ8DhfDv3fk7lp8fPp8jXP58JRN4f3bFvjMCxayUExiQpbyJLJwesQvMxniarC5fG2tiWXM8IiLTQnyeTcLEoQsjaplAj4rP8IHd3eo7nA37VVMCQnxf1g';
const BULLETIN_ID = 10;
const USER_ID = 4;

async function demoStomp() {
  const client = new Client({
    webSocketFactory: () =>
      new WebSocket(WS_URL + `?token=${JWT_TOKEN}`),

    reconnectDelay: 0,
    debug: () => { }
  });

  client.onConnect = (frame: Frame) => {
    var step = 0;
    console.log('✅ STOMP CONNECTED:', frame.headers);

    client.subscribe(`/topic/${BULLETIN_ID}`, (msg: IMessage) => {
      const resp = JSON.parse(msg.body);
      const thingId = resp.id;
      console.log('📥 Got response:', resp);

      if (step == 0) {
        const updatePayload = {
          id: thingId,
          content: "Updated From TS!",
        }
        client.publish({
          destination: `/app/update/${BULLETIN_ID}`,
          body: JSON.stringify(updatePayload),
          headers: {
            'content-type': 'application/json'
          }
        });
        console.log('📤 Sent updateThing:', updatePayload);
        step = 1;

        setTimeout(() => {
          client.deactivate();
          console.log('🛑 Disconnected');
        }, 2000);
      }

      if (step == 2) {
        const deletePayload = {
          id: thingId,
        }
        client.publish({
          destination: `/app/delete/${BULLETIN_ID}`,
          body: JSON.stringify(deletePayload),
          headers: {
            'content-type': 'application/json'
          }
        });
        console.log('📤 Sent deleteThing:', deletePayload);
      }
    });

    const createPayload = {
      content: 'Hello from TS!',
      bulletinId: BULLETIN_ID,
      userId: USER_ID
    };

    client.publish({
      destination: `/app/create/${BULLETIN_ID}`,
      body: JSON.stringify(createPayload),
      headers: {
        'content-type': 'application/json'
      }
    });
    console.log('📤 Sent createThing:', createPayload);
  };

  client.onStompError = (frame: Frame & { body: string }) => {
    console.error('❌ STOMP ERROR:', frame.headers['message'], frame.body);
    client.deactivate();
  };

  client.onWebSocketError = (evt: any) => {
    console.error('❌ WS ERROR:', evt);
  };

  client.activate();
}

demoStomp();
