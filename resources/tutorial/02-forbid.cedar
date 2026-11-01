// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/StepForbid.tsx

permit(
  principal == User::"alice", 
  action    == Action::"view", 
  resource  == Photo::"VacationPhoto94.jpg"
);

forbid(
  principal == User::"alice", 
  action    == Action::"view", 
  resource  == Photo::"VacationPhoto94.jpg"
);
