# Java unit-testing playground
### Evaluated mocking of static classes in Spock framework using Powermock

#### Mock static methods from Java system classes
- there some limitations from PowerMock to mock java system related class in groovy (mentioned [here](https://github.com/spockframework/spock/issues/1014))
- showcase a workaround using `PowerMock.replace()` (`PowerMock.stub()` will work too but there's no way for verification)
- example in DnsLookupServiceTest's testcase 1

#### Mock static methods for non-Java system classes
- example in DnsLookupServiceTest's testcase 2
