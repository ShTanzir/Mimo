Put your final MIMO app icon here as:

    assets/Mimo.png

Recommended: a square PNG, at least 512x512px, with the subject centered
(the build script pads/crops it into a circle for the round icon, and
scales it down for every mipmap density automatically).

Once this file exists, GitHub Actions will pick it up automatically on the
next push — no other change needed. See .github/workflows/build.yml,
step "Generate app icons from assets/Mimo.png".
