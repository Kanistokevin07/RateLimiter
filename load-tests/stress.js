import http from "k6/http";
import { check } from "k6";

const BASE_URL = "http://localhost:7070";

export const options = {
    stages: [
        { duration: "30s", target: 100 },
        { duration: "30s", target: 300 },
        { duration: "30s", target: 600 },
        { duration: "30s", target: 1000 },
        { duration: "30s", target: 0 },
    ],

    thresholds: {
        http_req_duration: ["p(95)<500"],
    }
};

export function setup() {

    const payload = JSON.stringify({
        clientId: "stress-client",
        algorithmType: "TOKEN_BUCKET",
        capacity: 500,
        refillTokensPerSecond: 100,
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
        clientId: "stress-client",
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