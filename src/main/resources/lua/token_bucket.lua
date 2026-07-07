-- KEYS[1] = bucket key

-- ARGV[1] = capacity
-- ARGV[2] = refill rate
-- ARGV[3] = current time
-- ARGV[4] = requested tokens

local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local bucket = redis.call("HGETALL", key)

local availableTokens
local lastRefill

if next(bucket) == nil then

    availableTokens = capacity
    lastRefill = now

else

    local map = {}

    for i = 1,#bucket,2 do
        map[bucket[i]] = bucket[i+1]
    end

    availableTokens =
        tonumber(map["availableTokens"])

    lastRefill =
        tonumber(map["lastRefillTimestamp"])

end

if now > lastRefill then

    local elapsed = now - lastRefill

    local tokensToAdd =
            elapsed * refillRate

    availableTokens =
        math.min(
            capacity,
            availableTokens + tokensToAdd
        )

    lastRefill = now

end

local allowed = 0

if availableTokens >= requested then

    availableTokens =
        availableTokens - requested

    allowed = 1

end

redis.call(
    "HMSET",
    key,
    "availableTokens",
    availableTokens,
    "lastRefillTimestamp",
    lastRefill
)

return {
    allowed,
    availableTokens,
    lastRefill
}