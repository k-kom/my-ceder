// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/StepABAC.tsx

permit(
  principal,
  action == Action::"view",
  resource
)
when {resource.accessLevel == "public" && principal.location == "USA"};
