// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/StepABAC2.tsx

permit(
  principal, 
  action in [Action::"view", Action::"edit", Action::"delete"], 
  resource 
)
when {
  resource.owner == principal.id
};
