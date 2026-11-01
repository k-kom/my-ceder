# Cedar tutorial policies

Copied from the local cedar-website checkout, revision 8d5905fda8c52268a41505efe44e35f908a80388.
Source: https://github.com/cedar-policy/cedar-website/tree/8d5905fda8c52268a41505efe44e35f908a80388/src/routes/tutorial

Files follow the step order in `src/App.tsx`:

1. Policy structure
2. Forbid
3. Sets
4. Undefined scope
5. RBAC
6. ABAC, part 1
7. ABAC, part 2
8. Context
9. Schema
10. Policy templates (four separate examples)

Policy bodies preserve the tutorial examples. Step 8's JavaScript interpolations
are resolved to the tutorial's UIDs. `09-schema.json` preserves its schema.
`requests.json` contains the original request/entity/context data for steps 1–8
and added authorization requests for steps 9–10 (not supplied upstream).
Step 9 demonstrates authorization only, without schema validation.
Step 2 intentionally denies access.

`my-cedar.main/tutorial-results` also evaluates the step 10 template using Java
`TemplateLink` and `LinkValue`, binding Alice to `Photo::"pic.jpg"`.
The template uses resource equality; its preceding static examples use folder
membership. These differences are preserved from the tutorial.

Run examples with `clojure -M:run` and tests with `clojure -M:test`.
Upstream Apache-2.0 license and NOTICE are included here.
