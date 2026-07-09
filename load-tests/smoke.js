import http from "k6/http";
import { check } from "k6";

const BASE_URL = "http://localhost:7070";

export function setup() {

    const client = {
        clientId: "k6-client",
        algorithmType: "TOKEN_BUCKET",
        capacity: 100,
        refillTokensPerSecond: 10,
        windowSizeSeconds: 0,
        enabled: true
    };

    http.post(
        `${BASE_URL}/clients`,
        JSON.stringify(client),
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    );
}

export default function () {

    const response = http.post(
        `${BASE_URL}/rate-limit`,
        JSON.stringify({
            clientId: "k6-client",
            tokensRequested: 1
        }),
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    );

    check(response, {
        "status is 200 or 429": (r) =>
            r.status === 200 || r.status === 429
    });
}