# Ray Tracing Engine — ISE5786

A from-scratch **3D ray tracing renderer** written in **Java**. The engine casts rays through a virtual camera, finds intersections with geometric primitives, and shades pixels using a Phong-style illumination model — including shadows, reflection, and transparency.

Built as part of the *Introduction to Software Engineering* course, with emphasis on clean OOP design, design patterns, and performance optimizations (anti-aliasing and multi-threading).

---

## Features

| Area | Capabilities |
|------|----------------|
| **Geometries** | Sphere, Plane, Triangle, Polygon, Tube, Cylinder (+ composite collection) |
| **Lighting** | Ambient, Directional, Point, and Spot lights |
| **Shading** | Diffuse & specular (Phong), emission, shadows |
| **Global effects** | Recursive reflection and refraction / transparency |
| **Anti-aliasing** | Super-sampling (regular / jittered) and Adaptive Super-Sampling |
| **Performance** | Multi-threaded rendering (manual threads or parallel streams) |
| **Scenes** | Programmatic API + JSON scene loading (Gson) |
| **Output** | PNG image export via `ImageWriter` |
| **Quality** | Extensive JUnit 5 unit & integration tests |

---

## Tech Stack

- **Language:** Java 17+
- **IDE:** Eclipse
- **Testing:** JUnit 5 (Jupiter)
- **JSON:** Google Gson
- **Image I/O:** `java.awt` / `javax.imageio` (PNG)

---

## Architecture

The project is organized into cohesive packages with clear responsibilities:

```
src/
├── primitives/     # Point, Vector, Ray, Color, Material, Double3, Util
├── geometries/
│   ├── api/        # Intersectable, Geometry (contracts)
│   └── impl/       # Concrete shapes + Geometries composite
├── lighting/       # Light hierarchy + LightSource interface
├── scene/          # Scene model + JSON SceneDescriptor
├── sampling/       # SamplingGrid, Blackboard (anti-aliasing infrastructure)
└── renderer/       # Camera, RayTracer, ImageWriter, PixelManager
unittests/          # JUnit tests mirroring the source packages
scenes/             # Sample JSON scene files
images/             # Generated PNG output (created at runtime)
```

### Rendering pipeline

```
Scene (geometries + lights)
        │
        ▼
Camera (Builder) ──► construct ray(s) per pixel
        │
        ▼
RayTracer ──► find closest intersection
        │
        ▼
Shading (local Phong + shadows)
        │
        ▼
Global effects (reflection / refraction, recursive)
        │
        ▼
ImageWriter ──► images/<name>.png
```

1. **Camera** builds one or more rays through each pixel (single ray, super-sampling beam, or adaptive subdivision).
2. **RayTracer** finds the closest intersection with the scene geometries.
3. **Local effects** compute ambient, diffuse, and specular contributions from each light, with shadow rays.
4. **Global effects** recursively add reflection and transparency up to a depth / attenuation limit.
5. The final color is written to a PNG file under `images/`.

---

## Design Patterns

| Pattern | Where | Why |
|---------|--------|-----|
| **Builder** | `Camera.Builder` | Complex camera setup (location, orientation, VP size/distance, resolution, tracer, sampling, threads) with fluent chaining and validation in `build()`. |
| **Fluent setters (chaining)** | `Scene`, `Geometry`, `Material`, lights | Readable scene construction: `new Sphere(...).setEmission(...).setMaterial(...)`. |
| **Strategy** | `RayTracerBase` / `SimpleRayTracer` + `RayTracerType` | Swap tracing algorithms without changing the camera API. |
| **Template Method / NVI** | `Intersectable` | Public `findIntersections` / `calcIntersections` are final; subclasses implement `calcIntersectionsHelper`. |
| **Composite** | `Geometries` | Treat a collection of shapes as a single `Intersectable`. |
| **Inheritance hierarchy** | Lights & geometries | Shared behavior in base classes (`Light`, `RadialGeometry`, `Geometry`) with specialized subclasses. |
| **Factory-style selection** | `Camera.Builder.setRayTracer(scene, type)` | Instantiates the correct tracer from `RayTracerType`. |

### Camera Builder — example

```java
Camera camera = Camera.getBuilder()
    .setLocation(new Point(0, 0, 1000))
    .setDirection(new Point(0, 0, -100))
    .setVpSize(200, 200)
    .setVpDistance(1000)
    .setResolution(800, 800)
    .setRayTracer(scene, RayTracerType.SIMPLE)
    .setMultithreading(-2)          // use available CPU cores
    .setDebugPrint(1)               // progress print interval
    .setAdaptiveSuperSampling(true, 4, 0.05)
    .build();

camera.renderImage()
      .writeToImage("my_scene");   // → images/my_scene.png
```

### Multi-threading modes

Configured via `setMultithreading(int)`:

| Value | Behavior |
|-------|----------|
| `0` | Single-threaded rendering |
| `-1` | Parallel `IntStream` (ForkJoinPool) |
| `-2` | Auto: raw threads ≈ `availableProcessors - spare` |
| `N > 0` | Exactly `N` worker threads |

Thread-safe pixel assignment is handled by `PixelManager`.

---

## Getting Started

### Prerequisites

- JDK 17 or newer
- Eclipse IDE (or any IDE that can run JUnit 5)
- **JUnit 5** and **Gson** on the classpath (add via Eclipse: *Project → Properties → Java Build Path → Libraries*)

### Import the project

1. Clone the repository.
2. In Eclipse: **File → Import → Existing Projects into Workspace**.
3. Select the project root and finish.

### Run unit tests

Most features are exercised and demonstrated through JUnit tests under `unittests/`.

**Eclipse:** right-click a test class (or the `unittests` folder) → **Run As → JUnit Test**.

Suggested demos:

| Test class | What it produces |
|------------|------------------|
| `renderer.OurCustomImageTest` | Custom scenes with shadows, reflection & glass |
| `renderer.ShadowTests` | Shadow casting |
| `renderer.TransparencyReflectionTests` | Reflection / refraction |
| `sampling.SuperSamplingTests` | Anti-aliasing comparisons |
| `sampling.AirplaneWindowView1Test` | Adaptive SS + multi-threading |

Rendered images are written to the `images/` directory at the project root.

### Minimal render snippet

```java
Scene scene = new Scene("Demo")
    .setBackground(new Color(10, 15, 25))
    .setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

scene._geometries.add(
    new Sphere(new Point(0, 0, -50), 30)
        .setEmission(new Color(100, 0, 0))
        .setMaterial(new Material().setKD(0.5).setKS(0.5).setNShininess(80))
);

scene.lights.add(
    new SpotLight(new Color(700, 700, 700), new Point(50, 50, 100), new Vector(-1, -1, -2))
);

Camera.getBuilder()
    .setRayTracer(scene, RayTracerType.SIMPLE)
    .setLocation(new Point(0, 0, 1000))
    .setDirection(new Point(0, 0, -100))
    .setVpDistance(1000).setVpSize(200, 200)
    .setResolution(600, 600)
    .build()
    .renderImage()
    .writeToImage("demo");
```

### JSON scenes

Sample scenes live in `scenes/`. Load them with:

```java
Scene scene = SceneDescriptor.loadSceneFromJSON("scenes/basicRenderTestTwoColors.json");
```

See `RenderTests.testBasicRenderJson` for an end-to-end example.

---

## Project Structure at a Glance

```
ISE5786_4450_0559/
├── src/                 # Production source code
├── unittests/           # JUnit 5 tests & visual demos
├── scenes/              # JSON scene definitions
├── images/              # PNG output (runtime)
├── doc/                 # Generated JavaDoc
└── README.md
```

---

## Authors

Hadas Wertheimer & Shani Rotem
Course project — ISE5786 (Introduction to Software Engineering).

---

## License

Educational project for academic use.
