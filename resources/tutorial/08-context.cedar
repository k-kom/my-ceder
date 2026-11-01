// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/StepContext.tsx

permit(
    principal in User::"alice", 
    action in [Action::"update", Action::"delete"],
    resource == Photo::"flower.jpg")
when {
    context.mfa_authenticated == true &&
    context.request_client_ip == "222.222.222.222"
};
