import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

export let allowed = new Counter("allowed_requests");
export let rejected = new Counter("rejected_requests");

const BASE_URL = "http://localhost:7070";

export const options = {
    scenarios: {
        load_test: {
            executor: "constant-vus",
            vus: 100,
            duration: "30s",
        },
    },
    checks: [
        "rate>0.99"
    ],

    thresholds: {

        http_req_duration: ["p(95)<200"],
    },
};

export function setup() {

    const payload = JSON.stringify({
        clientId: "load-client",
        algorithmType: "TOKEN_BUCKET",
        capacity: 100,
        refillTokensPerSecond: 20,
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
        clientId: "load-client",
        tokensRequested: 1
    });

    const response = http.post(
        `${BASE_URL}/rate-limit`,
        payload,
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    );

    if (response.status === 200) {
        allowed.add(1);
    }

    if (response.status === 429) {
        rejected.add(1);
    }

    check(response, {
        "status is 200 or 429": (r) =>
            r.status === 200 || r.status === 429,

        "response time < 200ms": (r) =>
            r.timings.duration < 200
    });
}