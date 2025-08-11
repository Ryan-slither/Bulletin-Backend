import { Client, Frame, IMessage } from '@stomp/stompjs';
import WebSocket from 'ws';

const WS_URL = 'ws://localhost:8080/ws/userlike';
const JWT_TOKEN = 'eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJidWxsZXRpbiIsInN1YiI6IjUiLCJleHAiOjE3NTUwMzE5OTcsImlhdCI6MTc1MjQzOTk5N30.kaGWBd02hyLaD0wBcosPCD9MfOrgceQFSKPaMHfBvtCADZeIZXiViGv5uWEpGFSSk4uTV9P81Vj4UhUTeBsSYMqpBs7wwHpUpxoOllA7cFklWEwSCl-xBrnelV22eJIG8BQaGN6XgI-8MvH1696qoyn9jWrFFZsIDCRfExZWueaDuJ4BOuV7BRNtNyGm4EmAXmCeQ8u8ErSGMODc40wrj_4Pfs_NPQN5EX-gM-0jQbdFf1iMDxieeRX_KkkIkoLQok9PzHA-LXr0YWgwMgW7RFmV0ubA5FrZqYKKM_MkxymvFtMom1GtudBBHAKapimKo8jToYTulvT5O6z1A-2xEQ';
const BULLETIN_ID = 7;
const USER_ID = 5;
const THING_ID = 55;

async function testUserLikeSocket() {
    const client = new Client({
        webSocketFactory: () => new WebSocket(WS_URL, {
            headers: { Authorization: `Bearer ${JWT_TOKEN}` }
        }),
        connectHeaders: {
            Authorization: `Bearer ${JWT_TOKEN}`
        },
        reconnectDelay: 0,
    });

    client.onStompError = (frame: Frame & { body: string }) => {
        console.error('❌ STOMP ERROR:', frame.headers['message'], frame.body);
        client.deactivate();
    };
    client.onWebSocketError = (evt: any) => {
        console.error('❌ WS ERROR:', evt);
    };

    client.onConnect = (frame: Frame) => {
        console.log('✅ STOMP CONNECTED:', frame.headers);

        let step = 0;

        client.subscribe(`/topic/${BULLETIN_ID}`, (msg: IMessage) => {
            const payload = JSON.parse(msg.body);
            console.log('📥 Received:', payload);

            if (step === 0) {
                step = 1;
                const likeId = payload.id;

                client.publish({
                    destination: `/app/delete/${BULLETIN_ID}`,
                    headers: { 'content-type': 'application/json' },
                    body: JSON.stringify({ id: likeId })
                });
                console.log('📤 Sent DeleteUserLikeMessage:', { id: likeId })

                setTimeout(() => {
                    client.deactivate();
                    console.log('🛑 Disconnected');
                }, 2000);

            }
        });

        const createMessage = {
            userId: USER_ID,
            thingId: THING_ID
        };
        client.publish({
            destination: `/app/create/${BULLETIN_ID}`,
            headers: { 'content-type': 'application/json' },
            body: JSON.stringify(createMessage)
        });
        console.log('📤 Sent CreateUserLikeMessage:', createMessage);
    };

    client.activate();
}

testUserLikeSocket().catch(err => console.error(err));
