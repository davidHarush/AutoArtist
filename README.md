<h1>AutoArtist</h1>

<p>This is a Kotlin Multiplatform project targeting Android and iOS.</p>

<h2>Project Structure</h2>
<ul>
  <li><code>/composeApp</code> contains code shared across the Compose Multiplatform applications.
    <ul>
      <li><code>commonMain</code>: Code common for all targets.</li>
      <li>Other platform-specific folders, like <code>androidMain</code> and <code>iosMain</code>, contain code compiled only for the respective platform. For example, platform-specific libraries like Apple’s CoreCrypto would be added to <code>iosMain</code>.</li>
    </ul>
  </li>
  <li><code>/iosApp</code> contains the iOS application entry point. Even if the UI is shared with Compose Multiplatform, this folder is required for the iOS-specific app logic and SwiftUI integrations.</li>
  <li><code>/androidApp</code> contains the Android application entry point and any Android-specific implementation.</li>
</ul>

<h2>Key Features</h2>
<ol>
  <li><strong>Compose Multiplatform UI:</strong> Unified user interface for Android and iOS.</li>
  <li><strong>Dynamic Questionnaires:</strong> Users answer questions to define preferences (e.g., colors, themes).</li>
  <li><strong>AI-Powered Prompts:</strong> User inputs are sent to GPT API to generate detailed image prompts.</li>
  <li><strong>DALL-E 3 Integration:</strong> Generated prompts are used to create unique images with DALL-E 3.</li>
  <li><strong>Text-to-Audio:</strong> Prompts are converted into MP4 audio files using a text-to-audio service.</li>
  <li><strong>Room Database:</strong> Prompts, images, and audio files are stored locally for a gallery view.</li>
  <li><strong>Dependency Injection:</strong> Koin is used for managing dependencies efficiently across modules.</li>
</ol>

<h2>How It Works</h2>
<ol>
  <li>User answers a series of questions.</li>
  <li>Answers are sent to GPT API for a descriptive image prompt.</li>
  <li>Prompt is sent to DALL-E 3 API to generate an image.</li>
  <li>Prompt is also sent to a text-to-audio service to create an MP4 file.</li>
  <li>Generated image and MP4 file are stored in Room for later viewing.</li>
</ol>

<h2>Technologies Used</h2>
<ul>
  <li><strong>Kotlin Multiplatform Mobile (KMM):</strong> For shared codebase across platforms.</li>
  <li><strong>Jetpack Compose Multiplatform:</strong> For building the user interface.</li>
  <li><strong>OpenAI GPT API:</strong> To generate descriptive prompts based on user input.</li>
  <li><strong>DALL-E 3 API:</strong> To generate images from the prompts.</li>
  <li><strong>Text-to-Audio API:</strong> To convert prompts into audio files.</li>
  <li><strong>Room Database:</strong> To store prompts, images, and audio files for later use.</li>
  <li><strong>Koin:</strong> For dependency injection and efficient module management.</li>
</ul>

<h2>Learn More</h2>
<p>Learn more about <a href="https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html">Kotlin Multiplatform</a>.</p>
