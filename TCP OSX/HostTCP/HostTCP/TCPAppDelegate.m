//
//  TCPAppDelegate.m
//  HostTCP
//
//  Created by Jad Kawwa on 2013-09-21.
//  Copyright (c) 2013 Jad Kawwa. All rights reserved.
//

#import "TCPAppDelegate.h"
#include <CoreFoundation/CoreFoundation.h>
#include <sys/socket.h>
#include <netinet/in.h>

#import "iTunes.h"
#import <Foundation/Foundation.h>
#import <ScriptingBridge/ScriptingBridge.h>

@implementation TCPAppDelegate

@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize managedObjectContext = _managedObjectContext;

iTunesApplication * iTunes;

int processCmd(char *cmd, NSOutputStream *outputBuffer)
{
    NSString *NScmd = [NSString stringWithUTF8String:cmd];
    if([NScmd rangeOfString:@"info"].location == 0)
    {
        iTunesTrack *current = iTunes.currentTrack;
        NSString *name = current.name;
        NSInteger position = iTunes.playerPosition;
        NSString *posS = [NSString stringWithFormat:@"%d", (int)position*1000];
        NSMutableString *finalsend;
        finalsend = [NSString stringWithFormat:@"play:%@:%@\n", name, posS];
        char *finalsendutf = [finalsend UTF8String];
        [outputBuffer write:(uint8*)finalsendutf maxLength:strlen(finalsendutf)];
        NSLog(@"WRITING: %s",finalsendutf);
    }
    if([NScmd rangeOfString:@"play"].location == 0)
    {
        NSString *inputName = [NScmd componentsSeparatedByString:@":"][1];
        NSString *inputTime = [NScmd componentsSeparatedByString:@":"][2];
        SBElementArray *allSources = iTunes.sources;
        iTunesSource *iTunesSource = allSources[0];
        iTunesLibraryPlaylist *iTunesLibrary = iTunesSource.libraryPlaylists[0];
        SBElementArray *allSongs = iTunesLibrary.fileTracks;
        for (iTunesTrack *song in allSongs)
        {
            if([song.name isEqualToString:inputName])
            {
                [song playOnce:true];
                iTunes.playerPosition = [inputTime integerValue]/1000;
                break;
            }
            //          printf("%s\n",[song.name UTF8String]);
        }
        
    }
    else if([NScmd isEqualToString:@"pause\n"])
    {
        [iTunes playpause];
    }
    else if([NScmd isEqualToString:@"next\n"])
    {
        [iTunes nextTrack];
    }
    else if([NScmd isEqualToString:@"previous\n"])
    {
        [iTunes previousTrack];
    }
        
        
    return 0;
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    // Insert code here to initialize your application
    //Ping Test
//    int i;
//    for (i=1;i<255;i++)
//    {
//        CFReadStreamRef readStream;
//        CFWriteStreamRef writeStream;
//        NSInputStream *inputStream;
//        NSOutputStream *outputStream;
//        char iptest[15];
//        sprintf(iptest,"192.168.1.%d",i);
//        NSString *ipaddress =[NSString stringWithUTF8String:iptest];
//        CFStreamCreatePairWithSocketToHost(NULL, (__bridge CFStringRef)ipaddress, 1000, &readStream, &writeStream);
//        inputStream = (__bridge NSInputStream *)readStream;
//        outputStream = (__bridge NSOutputStream *)writeStream;
//        [inputStream setDelegate:self];
//        [outputStream setDelegate:self];
//        [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
//        [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
//        
//        [inputStream open];
//        [outputStream open];
//        printf("Initialized Connection with ip: %s\n",iptest);
//        char *testmessage = "Pinging\n";
//        char inputread[255];
//        printf("Sent Message\n");
//        [outputStream write:(uint8 *)testmessage maxLength:255];
//        [inputStream read:(uint8 *)inputread maxLength:255];
//        printf("Received Message: %s\b",inputread);
//    }
    
    //////////////////
    //iTunes Control Test
    iTunes = [SBApplication applicationWithBundleIdentifier:@"com.apple.iTunes"];
//    processCmd("play:Breathing:0");
    
    //////////////////
    CFSocketRef myipv4cfsock = CFSocketCreate(
                                              kCFAllocatorDefault,
                                              PF_INET,
                                              SOCK_STREAM,
                                              IPPROTO_TCP,
                                              kCFSocketAcceptCallBack, handleConnect, NULL);
    CFSocketRef myipv6cfsock = CFSocketCreate(
                                              kCFAllocatorDefault,
                                              PF_INET6,
                                              SOCK_STREAM,
                                              IPPROTO_TCP,
                                              kCFSocketAcceptCallBack, handleConnect, NULL);
    NSLog(@"Created Sockets");
    struct sockaddr_in sin;
    
    memset(&sin, 0, sizeof(sin));
    sin.sin_len = sizeof(sin);
    sin.sin_family = AF_INET; /* Address family */
//    sin.sin_port = htons(0); /* Or a specific port */
    sin.sin_port = htons(27021);
    sin.sin_addr.s_addr= INADDR_ANY;
    
    CFDataRef sincfd = CFDataCreate(
                                    kCFAllocatorDefault,
                                    (UInt8 *)&sin,
                                    sizeof(sin));
    
    CFSocketSetAddress(myipv4cfsock, sincfd);
    CFRelease(sincfd);
    
    struct sockaddr_in6 sin6;
    
    memset(&sin6, 0, sizeof(sin6));
    sin6.sin6_len = sizeof(sin6);
    sin6.sin6_family = AF_INET6; /* Address family */
//    sin6.sin6_port = htons(0); /* Or a specific port */
    sin6.sin6_port = htons(27015);
    sin6.sin6_addr = in6addr_any;
    
    CFDataRef sin6cfd = CFDataCreate(
                                     kCFAllocatorDefault,
                                     (UInt8 *)&sin6,
                                     sizeof(sin6));
    
    CFSocketSetAddress(myipv6cfsock, sin6cfd);
    CFRelease(sin6cfd);
    
    NSLog(@"Sockets Initialized");
//    NSLog([[NSString alloc] initWithUTF8String:(sin.sin_addr.s_addr)]);
    
    CFRunLoopSourceRef socketsource = CFSocketCreateRunLoopSource(
                                                                  kCFAllocatorDefault,
                                                                  myipv4cfsock,
                                                                  0);
    
    CFRunLoopAddSource(
                       CFRunLoopGetCurrent(),
                       socketsource,
                       kCFRunLoopDefaultMode);
    
    CFRunLoopSourceRef socketsource6 = CFSocketCreateRunLoopSource(
                                                                   kCFAllocatorDefault,
                                                                   myipv6cfsock,
                                                                   0);
    
    CFRunLoopAddSource(
                       CFRunLoopGetCurrent(),
                       socketsource6,
                       kCFRunLoopDefaultMode);
    
    NSLog(@"Started Loop");
    
}

void handleConnect (
                    CFSocketRef s,
                    CFSocketCallBackType callbackType,
                    CFDataRef address,
                    const void *data,
                    void *info
                    )
{
    NSLog(@"handleConnect called with callbackType = %li", callbackType);
    
    BOOL canAccept = callbackType & kCFSocketAcceptCallBack;
    BOOL canWrite = callbackType & kCFSocketWriteCallBack;
    BOOL canRead = callbackType & kCFSocketReadCallBack;
    NSLog(@" ... acceptable? %@  .. readable? %@ .. writeable? %@", canAccept?@"yes":@"no", canRead?@"yes":@"no", canWrite?@"yes":@"no" );
    
    if( canAccept)
    {
        char buffer[255];
        inet_ntop(AF_INET, address, buffer, INET_ADDRSTRLEN);
        NSLog(@"[%@] Accepted a socket connection from remote host. Address = %s", @"JKServer", buffer);
        /**
         "which means that a new connection has been accepted. In this case, the data parameter of the callback is a pointer to a CFSocketNativeHandle value (an integer socket number) representing the socket.
         
         To handle the new incoming connections, you can use the CFStream, NSStream, or CFSocket APIs. The stream-based APIs are strongly recommended."
         */
        
        // "1. Create read and write streams for the socket with the CFStreamCreatePairWithSocket function."
        CFReadStreamRef clientInput = NULL;
        CFWriteStreamRef clientOutput = NULL;
        
        // for an AcceptCallBack, the data parameter is a pointer to a CFSocketNativeHandle
        CFSocketNativeHandle nativeSocketHandle = *(CFSocketNativeHandle *)data;
        
        CFStreamCreatePairWithSocket(kCFAllocatorDefault, nativeSocketHandle, &clientInput, &clientOutput);
        
        // "2. Cast the streams to an NSInputStream object and an NSOutputStream object if you are working in Cocoa."
        NSInputStream *inputBuffer = (__bridge NSInputStream *)(clientInput);
        NSOutputStream *outputBuffer = (__bridge NSOutputStream *)(clientOutput);
        // "3. Use the streams as described in “Writing a TCP-Based Client.”
//        outputBuffer = [NSMutableData dataWithData:[@"Hello" dataWithEncoding:NSUTF8StringEncoding]];

//        outputBytesWrittenRecently = 0;
//        ((NSOutputStream*)output).delegate = self;
        char input[255];

        memset(input, 0, 255);
//        [((NSInputStream*)inputBuffer) scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        [((NSInputStream*)inputBuffer) open];
        
//        [((NSOutputStream*)outputBuffer) scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        [((NSOutputStream*)outputBuffer) open];
        while (true)
        {
            input[0] = '\0';
            memset(input,0,255);
            [inputBuffer read:input maxLength:255];
            if(input[0]!='\0')
            {
                NSLog(@"READ: %s",input);
                processCmd(input,outputBuffer);
//                char *outputString = (char *)malloc(255*sizeof(char));
//                memset(outputString,'\0',255);
//                strcpy(outputString,"CONNECTED! CONGRATS! THIS IS JKServer\r\n");
//                [outputBuffer write:(uint8*)outputString maxLength:strlen(outputString)];
//                NSLog(@"WRITING: %s",outputString);
//                free(outputString);
            }
        }
        // MUST go last, undocumented Apple bug
    }
}

// Returns the directory the application uses to store the Core Data store file. This code uses a directory named "com.JK3.HostTCP" in the user's Application Support directory.
- (NSURL *)applicationFilesDirectory
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *appSupportURL = [[fileManager URLsForDirectory:NSApplicationSupportDirectory inDomains:NSUserDomainMask] lastObject];
    return [appSupportURL URLByAppendingPathComponent:@"com.JK3.HostTCP"];
}

// Creates if necessary and returns the managed object model for the application.
- (NSManagedObjectModel *)managedObjectModel
{
    if (_managedObjectModel) {
        return _managedObjectModel;
    }
	
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"HostTCP" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

// Returns the persistent store coordinator for the application. This implementation creates and return a coordinator, having added the store for the application to it. (The directory for the store is created, if necessary.)
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (_persistentStoreCoordinator) {
        return _persistentStoreCoordinator;
    }
    
    NSManagedObjectModel *mom = [self managedObjectModel];
    if (!mom) {
        NSLog(@"%@:%@ No model to generate a store from", [self class], NSStringFromSelector(_cmd));
        return nil;
    }
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *applicationFilesDirectory = [self applicationFilesDirectory];
    NSError *error = nil;
    
    NSDictionary *properties = [applicationFilesDirectory resourceValuesForKeys:@[NSURLIsDirectoryKey] error:&error];
    
    if (!properties) {
        BOOL ok = NO;
        if ([error code] == NSFileReadNoSuchFileError) {
            ok = [fileManager createDirectoryAtPath:[applicationFilesDirectory path] withIntermediateDirectories:YES attributes:nil error:&error];
        }
        if (!ok) {
            [[NSApplication sharedApplication] presentError:error];
            return nil;
        }
    } else {
        if (![properties[NSURLIsDirectoryKey] boolValue]) {
            // Customize and localize this error.
            NSString *failureDescription = [NSString stringWithFormat:@"Expected a folder to store application data, found a file (%@).", [applicationFilesDirectory path]];
            
            NSMutableDictionary *dict = [NSMutableDictionary dictionary];
            [dict setValue:failureDescription forKey:NSLocalizedDescriptionKey];
            error = [NSError errorWithDomain:@"YOUR_ERROR_DOMAIN" code:101 userInfo:dict];
            
            [[NSApplication sharedApplication] presentError:error];
            return nil;
        }
    }
    
    NSURL *url = [applicationFilesDirectory URLByAppendingPathComponent:@"HostTCP.storedata"];
    NSPersistentStoreCoordinator *coordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:mom];
    if (![coordinator addPersistentStoreWithType:NSXMLStoreType configuration:nil URL:url options:nil error:&error]) {
        [[NSApplication sharedApplication] presentError:error];
        return nil;
    }
    _persistentStoreCoordinator = coordinator;
    
    return _persistentStoreCoordinator;
}

// Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.) 
- (NSManagedObjectContext *)managedObjectContext
{
    if (_managedObjectContext) {
        return _managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (!coordinator) {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        [dict setValue:@"Failed to initialize the store" forKey:NSLocalizedDescriptionKey];
        [dict setValue:@"There was an error building up the data file." forKey:NSLocalizedFailureReasonErrorKey];
        NSError *error = [NSError errorWithDomain:@"YOUR_ERROR_DOMAIN" code:9999 userInfo:dict];
        [[NSApplication sharedApplication] presentError:error];
        return nil;
    }
    _managedObjectContext = [[NSManagedObjectContext alloc] init];
    [_managedObjectContext setPersistentStoreCoordinator:coordinator];

    return _managedObjectContext;
}

// Returns the NSUndoManager for the application. In this case, the manager returned is that of the managed object context for the application.
- (NSUndoManager *)windowWillReturnUndoManager:(NSWindow *)window
{
    return [[self managedObjectContext] undoManager];
}

// Performs the save action for the application, which is to send the save: message to the application's managed object context. Any encountered errors are presented to the user.
- (IBAction)saveAction:(id)sender
{
    NSError *error = nil;
    
    if (![[self managedObjectContext] commitEditing]) {
        NSLog(@"%@:%@ unable to commit editing before saving", [self class], NSStringFromSelector(_cmd));
    }
    
    if (![[self managedObjectContext] save:&error]) {
        [[NSApplication sharedApplication] presentError:error];
    }
}

- (NSApplicationTerminateReply)applicationShouldTerminate:(NSApplication *)sender
{
    // Save changes in the application's managed object context before the application terminates.
    
    if (!_managedObjectContext) {
        return NSTerminateNow;
    }
    
    if (![[self managedObjectContext] commitEditing]) {
        NSLog(@"%@:%@ unable to commit editing to terminate", [self class], NSStringFromSelector(_cmd));
        return NSTerminateCancel;
    }
    
    if (![[self managedObjectContext] hasChanges]) {
        return NSTerminateNow;
    }
    
    NSError *error = nil;
    if (![[self managedObjectContext] save:&error]) {

        // Customize this code block to include application-specific recovery steps.              
        BOOL result = [sender presentError:error];
        if (result) {
            return NSTerminateCancel;
        }

        NSString *question = NSLocalizedString(@"Could not save changes while quitting. Quit anyway?", @"Quit without saves error question message");
        NSString *info = NSLocalizedString(@"Quitting now will lose any changes you have made since the last successful save", @"Quit without saves error question info");
        NSString *quitButton = NSLocalizedString(@"Quit anyway", @"Quit anyway button title");
        NSString *cancelButton = NSLocalizedString(@"Cancel", @"Cancel button title");
        NSAlert *alert = [[NSAlert alloc] init];
        [alert setMessageText:question];
        [alert setInformativeText:info];
        [alert addButtonWithTitle:quitButton];
        [alert addButtonWithTitle:cancelButton];

        NSInteger answer = [alert runModal];
        
        if (answer == NSAlertAlternateReturn) {
            return NSTerminateCancel;
        }
    }

    return NSTerminateNow;
}

@end
