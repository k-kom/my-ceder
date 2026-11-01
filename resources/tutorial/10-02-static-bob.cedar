// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/PolicyTemplatesStep.tsx

permit (
    principal == User::"aa72347c-0f07-4015-b23c-551bab7371bb", // "Bob"
    action in [Action::"readFile", Action::"writeFile"],
    resource in Folder::"d5cf9de7-8f10-452b-9aef-a3b735da47ef"   // "Birthday Photos"
);
