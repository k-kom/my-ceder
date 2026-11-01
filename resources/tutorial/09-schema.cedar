// Copyright Cedar Contributors
// SPDX-License-Identifier: Apache-2.0
// Source: https://github.com/cedar-policy/cedar-website/blob/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial/SchemaTutorialStep.tsx

// Jane's friends can view all photos in her trips album

permit(
    principal in PhotoApp::UserGroup::"janeFriends",
    action in [PhotoApp::Action::"viewPhoto", PhotoApp::Action::"listPhotos"], 
    resource in PhotoApp::Album::"janeTrips"
);
