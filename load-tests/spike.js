import http from "k6/http";
import { check } from "k6";

const BASE_URL = "http://localhost:7070";

export const options = {
    stages: [
        { duration: "5s", target: 0 },
        { duration: "2s", target: 1000 },
        { duration: "20s", target: 1000 },
        { duration: "2s", target: 0 },
    ],

    thresholds: {
        http_req_duration: ["p(95)<500"],
    }
};

export function setup() {

    const payload = JSON.stringify({
        clientId: "spike-client",
        algorithmType: "TOKEN_BUCKET",
        capacity: 1000,
        refillTokensPerSecond: 1000,
        windowSizeSeconds: 0,
        enabled: true
    });

    http.post(
        `${BASE_URL}/clients`,
        payload,
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    );
}

export default function () {

    const payload = JSON.stringify({
        clientId: "spike-client",
        tokensRequested: 1
    });

    const params = {
        headers: {
            "Content-Type": "application/json"
        }
    };

    const res = http.post(
        `${BASE_URL}/rate-limit`,
        payload,
        params
    );

    check(res, {
        "status 200 or 429": (r) =>
            r.status === 200 || r.status === 429
    });
}