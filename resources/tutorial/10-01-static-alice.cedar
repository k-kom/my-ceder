// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/PolicyTemplatesStep.tsx

permit (
    principal == User::"973db890-092c-49e4-a9d0-912a4c0a20c7", // "Alice"
    action in [Action::"readFile", Action::"writeFile"],
    resource in Folder::"4e153865-0b24-4b77-94f7-9a7f24fa8a59"   // "Vacation Photos"
);
