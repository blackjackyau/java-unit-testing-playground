package com.blax.unit.testing.playground

import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.support.membermodification.MemberModifier
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * An example to mock java system related classes with Powermock, by taking InetAddress as an example
 */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest(DnsLookupService)
class DnsLookupServiceTest extends Specification {

    def inetAddressMock = Mock(InetAddressMock)

    def instance = new DnsLookupService();

    def setup() {
        MemberModifier.replace(
                MemberModifier.method(InetAddress, "getAllByName", String)).with(new InvocationHandler() {

            @Override
            Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return inetAddressMock.getAllByName(args[0])
            }
        })
    }

    /**
     * To test static method on java system classes
     * https://github.com/powermock/powermock/wiki/Mock-System will not work with Spock integration (groovy based ?)
     */
    def "testcase 1: should return success response with AddressResults[]"() {
        given:
            1 * inetAddressMock.getAllByName("test.localhost") >> ([
                Mock(InetAddress) {
                    getHostAddress() >> "88.88.88.88"
                    getHostName() >> "huat.com"
                    getCanonicalHostName() >> "8.internal.huat.com"
                }
            ] as InetAddress[])
        when:
            def responseEntity = instance.getIps("test.localhost")
        then:
            responseEntity.getStatusCode() == HttpStatus.OK
            responseEntity.getBody()[0].ip == "88.88.88.88"
            responseEntity.getBody()[0].host == "huat.com"
            responseEntity.getBody()[0].canonical == "8.internal.huat.com"
    }

    def "testcase 2: should return Exception when hostname is failing validation"() {
        given:
           PowerMockito.mockStatic(DnsLookupService)
           Mockito.when(DnsLookupService.validateHostName("malicious-hostname")).thenReturn(false)
        when:
            instance.getIps("malicious-hostname1")
        then:
            def exception = thrown(Exception)
            exception.message == "Illegal Hostname"
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            // https://github.com/powermock/powermock/wiki/Mockito#mocking-static-method
            PowerMockito.verifyStatic(DnsLookupService, Mockito.times(1));
            // DnsLookupService.validateHostName("malicious-hostname") == false works too but it is less intuitive
            DnsLookupService.validateHostName(argument.capture()) == false
            argument.getValue() == "malicious-hostname"
    }

    /**
     * Mock classes to reuse Mock feature for assertion
     */
    static class InetAddressMock {
        InetAddress[] getAllByName(String host) {
            throw new Exception("Not here");
        }
    }
}
