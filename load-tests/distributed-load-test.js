import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";


// ===============================
// Metrics
// ===============================

export const allowedRequests =
    new Counter("allowed_requests");

export const rejectedRequests =
    new Counter("rejected_requests");


export const successRate =
    new Rate("successful_responses");


export const latency =
    new Trend("rate_limiter_latency");


// ===============================
// Target Nginx
// ===============================

const BASE_URL = "http://localhost:8080";


// ===============================
// Load Profile
// ===============================

export const options = {
    scenarios: {

        distributed_load: {
            executor: "ramping-vus",
            stages: [
                {
                    duration: "10s",
                    target: 50
                },
                {
                    duration: "30s",
                    target: 200
                },
                {
                    duration: "30s",
                    target: 500
                },
                {
                    duration: "10s",
                    target: 0
                }
            ]
        }
    },


    thresholds: {

        http_req_duration: [
            "p(95)<200"
        ],

        successful_responses: [
            "rate>0.95"
        ]
    }
};



// ===============================
// Create Client Configuration
// ===============================

export function setup() {
    const payload = JSON.stringify({
        clientId: "distributed-client",
        algorithmType: "TOKEN_BUCKET",
        capacity: 100,
        refillTokensPerSecond: 20,
        windowSizeSeconds: 0,
        enabled: true
    });

    const response =
        http.post(
            `${BASE_URL}/clients`,
            payload,
            {
                headers: {

                    "Content-Type":
                        "application/json"

                }
            }
        );
    console.log(
        "Client setup status:",
        response.status
    );
}



// ===============================
// Test Requests
// ===============================

export default function () {
    const payload = JSON.stringify({
        clientId:
            "distributed-client",
        tokensRequested:
            1
    });

    const response =
        http.post(
            `${BASE_URL}/rate-limit`,
            payload,
            {
                headers: {
                    "Content-Type":
                        "application/json"
                }
            }
        );

    latency.add(
        response.timings.duration
    );
    if(response.status === 200){
        allowedRequests.add(1);
    }
    else if(response.status === 429){
        rejectedRequests.add(1);
    }

    successRate.add(
        response.status === 200 ||
        response.status === 429
    );

    check(response, {
        "valid response":
            (r)=>
                r.status === 200 ||
                r.status === 429,
        "latency under 200ms":
            (r)=>
                r.timings.duration < 200
    });
    sleep(0.1);
}