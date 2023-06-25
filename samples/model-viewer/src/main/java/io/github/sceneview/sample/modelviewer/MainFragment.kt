package io.github.sceneview.sample.modelviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import com.google.android.filament.android.TextureHelper
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.SKIP_BITMAP_COPY
import com.gorisse.thomas.lifecycle.lifecycle
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.nodes.ModelNode
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: SceneView
    lateinit var loadingView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById<SceneView>(R.id.sceneView).apply {
            setLifecycle(lifecycle)
        }
        loadingView = view.findViewById(R.id.loadingView)

        val textView = TextView(context).apply {
            text = "Hello World!"
            width = 10
            height = 10
            textSize = 12f
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            setBackgroundColor(ContextCompat.getColor(context, R.color.sceneview))
        }

        lifecycleScope.launchWhenCreated {
            val model = sceneView.modelLoader.loadModel("models/plane.glb")!!
            val modelNode = ModelNode(sceneView, model).apply {
                transform(
                    position = Position(z = -100.0f),
                    rotation = Rotation(x = 15.0f)
                )
            }

            // ToDo: Using Bitmap
            context?.let { context ->
                val loadTexture = loadTexture("Hello World", context, modelNode.engine)
                val material = sceneView.materialLoader.createViewMaterial(loadTexture, true, true)
                modelNode.setMaterialInstance(material)

                /*modelNode.materialInstances.first {
                    //it.setBaseTexture(loadTexture)
                    it.setParameter("baseColorMap", loadTexture, TextureSampler())
                    it.setParameter("baseColorIndex", 0)
                    it.setParameter("baseColorFactor", 1f, 0.0f, 0.0f, 1f)
                    true
                }*/
            }
            sceneView.addChildNode(modelNode)

            // ToDo: Using ViewNode
            /*val viewNode = ViewNode(
                sceneView = sceneView,
                view = textView,
                unlit = true,
                invertFrontFaceWinding = true
            ).apply {
                transform(
                    position = modelNode.position,
                    rotation = Rotation()
                )
                //scaleToUnitsCube(.0f)
            }
            //modelNode.addChildNode(viewNode)
            sceneView.addChildNode(viewNode)*/


            //ToDo: Using ViewStream and Texture
            /*val viewStream = ViewStream.Builder()
                .view(textView)
                .build(sceneView.engine, sceneView.windowViewManager)

            println("viewStream: ${viewStream.worldSize}")
            val texture = ViewTexture.Builder()
                .viewStream(viewStream)
                .build(sceneView.engine)

            val material = sceneView.materialLoader.createViewMaterial(texture, false, false)
            modelNode.setMaterialInstance(material)
            sceneView.addChildNode(modelNode)
            println("Size = ${modelNode.size}")

            viewStream.onSizeChanged = {
                modelNode.size = it
                println("viewStream size: $it")
            }*/

            // ToDo: Bitmap
            /*val bitmap = convertViewToByteBuffer(textView)

            val texture = Texture.Builder()
                .width(1000)
                .height(500)
                .build(sceneView.engine)

            texture.setImage(
                sceneView.engine,
                0,
                Texture.PixelBufferDescriptor(bitmap, Texture.Format.RGBA, Texture.Type.UBYTE)
            )

            val material = sceneView.materialLoader.createViewMaterial(texture, true, true)
            modelNode.setMaterialInstance(material)*/

            /* modelNode.materialInstances.first {
                 it.setBaseTexture(texture)
                 true
             }*/
            //sceneView.addChildNode(modelNode)

            loadingView.isGone = true
        }
    }

    private fun convertViewToBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun convertViewToByteBuffer(view: View): ByteBuffer {
        val bitmap = convertViewToBitmap(view)
        val byteBuffer = ByteBuffer.allocateDirect(bitmap.byteCount).order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(byteBuffer)
        byteBuffer.flip()
        return byteBuffer
    }

    private fun getTextBitmap(content: String, context: Context): Bitmap? {
        val textView = TextView(context)
        textView.text = content
        //crash when setting setBackgroundColor
        //textView.setBackgroundColor(Color.WHITE)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 160f)
        textView.setTextColor(Color.CYAN)
        textView.setDrawingCacheEnabled(true);
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
        val bitmap = Bitmap.createBitmap(textView.drawingCache)
        //千万别忘最后一步
        textView.destroyDrawingCache();
        return bitmap
    }

    private fun loadTexture(content: String, context: Context, engine: Engine): Texture {
        val bitmap: Bitmap = getTextBitmap(content, context)!!
        val texture = Texture.Builder()
            .width(bitmap.width)
            .height(bitmap.height)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.RGBA8)
            // This tells Filament to figure out the number of mip levels
            .levels(0xff)
            .build(engine)

        // TextureHelper offers a method that skips the copy of the bitmap into a ByteBuffer
        if (SKIP_BITMAP_COPY) {
            TextureHelper.setBitmap(engine, texture, 0, bitmap)
        } else {
            val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
            bitmap.copyPixelsToBuffer(buffer)
            // Do not forget to rewind the buffer!
            buffer.flip()

            val descriptor = Texture.PixelBufferDescriptor(
                buffer,
                Texture.Format.RGBA,
                Texture.Type.UBYTE
            )

            texture.setImage(engine, 0, descriptor)
        }

        texture.generateMipmaps(engine)

        return texture
    }
}