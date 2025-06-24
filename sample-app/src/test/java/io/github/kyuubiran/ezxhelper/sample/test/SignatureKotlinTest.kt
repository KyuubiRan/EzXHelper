package io.github.kyuubiran.ezxhelper.sample.test

import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import io.github.kyuubiran.ezxhelper.core.util.SignatureUtil
import org.junit.Test

class SignatureKotlinTest {

    @Test
    fun testGetSignature() {
        val mf = MethodFinder.fromClass(String::class)

        val m = mf.filterByName("toString").first()
        val signature = SignatureUtil.getSignature(m)
        assert(signature == "Ljava/lang/String;->toString()Ljava/lang/String;") {
            "Expected signature: Ljava/lang/String;->toString()Ljava/lang/String;, but got: $signature"
        }
        println("Signature: $signature")
    }

    @Test
    fun testSignatureToTypes1() {
        val signatures = SignatureUtil.signatureToTypes("IZ[I")
        assert(signatures.size == 3) {
            "Expected 3 signatures, but got: ${signatures.size}"
        }
        println("signatures.size = ${signatures.size}")
        assert(signatures[0] == Int::class.java && signatures[1] == Boolean::class.java && signatures[2] == IntArray::class.java) {
            "Expected signatures: [int, boolean, int[]], but got: $signatures"
        }
        println("signatures = $signatures")
    }

    @Test
    fun testSignatureToTypes2() {
        val signatures = SignatureUtil.signatureToTypes("[[Ljava/lang/String;Ljava/lang/Object;")
        assert(signatures.size == 2) {
            "Expected  signatures, but got: ${signatures.size}"
        }
        println("signatures.size = ${signatures.size}")
        assert(signatures[0] == Array<Array<String>>::class.java && signatures[1] == Any::class.java) {
            "Expected signatures: [int, boolean, int[]], but got: $signatures"
        }
        println("signatures = $signatures")
    }

    @Test
    fun testFinderFilterParamBySignature() {
        val mf = MethodFinder.fromClass(String::class)

        val m = mf.filterByName("substring")
            .filterByParamSignature("II")
            .first()

        assert(m.toString() == "public java.lang.String java.lang.String.substring(int,int)")

        println("Method: $m")

        val cf = ConstructorFinder.fromClass(String::class)

        val ctor = cf.filterByParamSignature("[BII").first()
        val str = ctor.newInstance("Hello world".toByteArray(), 0, 5) as String

        assert(str == "Hello")

        println("Constructor Result: $str")

        val ctor2 = cf.filterByParamSignature("").first()
        val str2 = ctor2.newInstance() as String

        assert(str2.isEmpty())
        println("Constructor Result with no params: $str2")
    }
}